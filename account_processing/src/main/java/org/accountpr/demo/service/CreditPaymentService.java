package org.accountpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.model.dto.PaymentDTO;
import org.accountpr.demo.model.enums.PaymentType;
import org.aop.annotations.LogDatasourceError;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditPaymentService {

    private final AccountService accountService;
    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @Transactional
    @LogDatasourceError(type="ERROR")
    public void createPaymentSchedule(Long accountId, BigDecimal loanAmount,
                                      BigDecimal annualInterestRate, Integer months) {
        AccountDTO account = accountService.getAccountById(accountId);

        if (!Boolean.TRUE.equals(account.getIsRecalc())) {
            log.warn("Account {} is not a credit account, skipping payment schedule", accountId);
            return;
        }

        BigDecimal monthlyRate = annualInterestRate
                .divide(BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, java.math.RoundingMode.HALF_UP);

        // annuity payment formula
        BigDecimal temp = BigDecimal.ONE.add(monthlyRate).pow(months);
        BigDecimal annuityPayment = loanAmount.multiply(monthlyRate.multiply(temp))
                .divide(temp.subtract(BigDecimal.ONE), 2, java.math.RoundingMode.HALF_UP);

        LocalDate paymentDate = LocalDate.now().plusMonths(1);

        log.info("Creating payment schedule for account {}: amount={}, rate={}%, months={}, monthly payment={}",
                accountId, loanAmount, annualInterestRate, months, annuityPayment);

        for (int i = 1; i <= months; i++) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .accountId(accountId)
                    .paymentDate(paymentDate)
                    .amount(annuityPayment)
                    .isCredit(true)
                    .payedAt(null)
                    .expired(false)
                    .type(PaymentType.INTEREST)
                    .build();

            paymentService.createPayment(paymentDTO);
            paymentDate = paymentDate.plusMonths(1);
        }

        log.info("Created {} payment schedules for account {}", months, accountId);
    }

    @Transactional
    public void processCreditPayments(AccountDTO account) {
        if (!Boolean.TRUE.equals(account.getIsRecalc())) {
            return;
        }
        LocalDate today = LocalDate.now();

            // find the nearest outstanding payment
        List<PaymentDTO> duePayments = paymentService.getPaymentsByAccountId(account.getId())
                .stream()
                .filter(p -> p.getIsCredit() && p.getPayedAt() == null && !p.getExpired())
                .filter(p -> !p.getPaymentDate().isAfter(today)) // expired or today
                //this was changed from that(p1, p2) -> p1.getPaymentDate().compareTo(p2.getPaymentDate())
                .sorted(Comparator.comparing(PaymentDTO::getPaymentDate))
                .toList();

        for (PaymentDTO payment : duePayments) {
            if (account.getBalance().compareTo(payment.getAmount()) >= 0) {
                // payment debit
                BigDecimal newBalance = account.getBalance().subtract(payment.getAmount());
                account.setBalance(newBalance);
                accountService.updateAccount(account.getId(), account);

                // mark the payment as paid
                payment.setPayedAt(LocalDateTime.now());
                paymentService.updatePayment(payment.getId(), payment);

                log.info("Processed credit payment {} for account {}", payment.getId(), account.getId());
            } else {
                // not enough money - mark as expired
                if (payment.getPaymentDate().isBefore(today)) {
                    payment.setExpired(true);
                    paymentService.updatePayment(payment.getId(), payment);
                    log.warn("Credit payment {} expired for account {}", payment.getId(), account.getId());
                }
            }
        }
    }


    @Transactional
    public void processExternalCreditPayment(Long accountId, BigDecimal amount) {
        AccountDTO account = accountService.getAccountById(accountId);

        if (!Boolean.TRUE.equals(account.getIsRecalc())) {
            log.warn("Account {} is not a credit account", accountId);
            return;
        }
        // find total amount of debt
        BigDecimal totalDebt = paymentService.getPaymentsByAccountId(accountId)
                .stream()
                .filter(p -> p.getIsCredit() && p.getPayedAt() == null && !p.getExpired())
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (amount.compareTo(totalDebt) == 0) {
            // full repayment
            completeAllPayments(accountId);
            log.info("Full credit repayment for account {}", accountId);
        } else {
            // Partial repayment - processed according to the algorithm
            processPartialRepayment(accountId, amount);
        }
    }

    private void completeAllPayments(Long accountId) {
        List<PaymentDTO> unpaidPayments = paymentService.getPaymentsByAccountId(accountId)
                .stream()
                .filter(p -> p.getIsCredit() && p.getPayedAt() == null && !p.getExpired())
                .toList();

        for (PaymentDTO payment : unpaidPayments) {
            payment.setPayedAt(LocalDateTime.now());
            paymentService.updatePayment(payment.getId(), payment);
        }
    }

    private void processPartialRepayment(Long accountId, BigDecimal amount) {
        List<PaymentDTO> unpaidPayments = paymentService.getPaymentsByAccountId(accountId)
                .stream()
                .filter(p -> p.getIsCredit() && p.getPayedAt() == null && !p.getExpired())
                //this was changed from that(p1, p2) -> p1.getPaymentDate().compareTo(p2.getPaymentDate())
                .sorted(Comparator.comparing(PaymentDTO::getPaymentDate))
                .toList();

        BigDecimal remainingAmount = amount;

        for (PaymentDTO payment : unpaidPayments) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) break;

            if (remainingAmount.compareTo(payment.getAmount()) >= 0) {
                //full payment of the current payment
                payment.setPayedAt(LocalDateTime.now());
                paymentService.updatePayment(payment.getId(), payment);
                remainingAmount = remainingAmount.subtract(payment.getAmount());
            } else {
                //partial payment - creating a partial payment
                createPartialPayment(payment, remainingAmount);
                remainingAmount = BigDecimal.ZERO;
            }
        }
    }
    @Scheduled(cron = "0 0 6 * * ?") //every day at 6 a.m.
    @Transactional
    public void checkOverduePayments() {
        LocalDate today = LocalDate.now();

        List<PaymentDTO> allPayments = paymentService.getAllPayments();

        for (PaymentDTO payment : allPayments) {
            if (payment.getIsCredit() &&
                    payment.getPayedAt() == null &&
                    !payment.getExpired() &&
                    payment.getPaymentDate().isBefore(today)) {

                payment.setExpired(true);
                paymentService.updatePayment(payment.getId(), payment);

                log.info("Marked payment {} as expired for account {}",
                        payment.getId(), payment.getAccountId());
            }
        }
    }
    private void createPartialPayment(PaymentDTO originalPayment, BigDecimal partialAmount) {
        PaymentDTO partialPayment = PaymentDTO.builder()
                .accountId(originalPayment.getAccountId())
                .paymentDate(LocalDate.now())
                .amount(partialAmount)
                .isCredit(true)
                .payedAt(LocalDateTime.now())
                .expired(false)
                .type(PaymentType.INTEREST)
                .build();

        paymentService.createPayment(partialPayment);

        // update original payment
        BigDecimal remainingAmount = originalPayment.getAmount().subtract(partialAmount);
        originalPayment.setAmount(remainingAmount);
        paymentService.updatePayment(originalPayment.getId(), originalPayment);
    }
}