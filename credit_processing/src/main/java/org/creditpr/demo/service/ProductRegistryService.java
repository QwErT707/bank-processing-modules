package org.creditpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.aop.annotations.LogDatasourceError;
import org.creditpr.demo.dto.PaymentRegistryDTO;
import org.creditpr.demo.dto.ProductRegistryDTO;
import org.creditpr.demo.model.ProductRegistry;
import org.creditpr.demo.repository.ProductRegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductRegistryService {
    private final ProductRegistryRepository repository;
    private final PaymentRegistryService paymentRegistryService;
    @LogDatasourceError(type = "ERROR")
    public ProductRegistryDTO createProductRegistry(ProductRegistryDTO dto){
        if(repository.existsByClientIdAndProductId(dto.getClientId(), dto.getProductId())){
            throw new IllegalArgumentException("Client already has this product");
        }
        ProductRegistry productRegistry= ProductRegistry.builder(
                dto.getClientId(),
                dto.getAccountId(),
                dto.getProductId(),
                dto.getAmount(),
                dto.getInterestRate(),
                dto.getMonthCount(),
                dto.getOpenDate()
        ).build();
        ProductRegistry saved= repository.save(productRegistry);
        return convertToDTO(saved);
    }

    public Optional<ProductRegistryDTO> getProductRegistryById(Long id){
        return repository.findById(id).map(this::convertToDTO);
    }

    public List<ProductRegistryDTO> getAllProductRegistries(){
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public List<ProductRegistryDTO> getProductRegistriesByClientId(Long clientId){
        return repository.findByClientId(clientId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public List<ProductRegistryDTO> getProductRegistriesByProductId(Long productId){
        return repository.findByProductId(productId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public ProductRegistryDTO updateProductRegistry(Long id, ProductRegistryDTO dto){
        ProductRegistry existing=repository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ProductRegistry not found with id: " + id));
        if(!existing.getClientId().equals(dto.getClientId())||!existing.getProductId().equals(dto.getProductId())){
            if (repository.existsByClientIdAndProductId(dto.getClientId(), dto.getProductId())){
                throw new IllegalArgumentException("Client already has this product");
            }
        }
        existing.setClientId(dto.getClientId());
        existing.setAccountId(dto.getAccountId());
        existing.setProductId(dto.getProductId());
        existing.setAmount(dto.getAmount());
        existing.setInterestRate(dto.getInterestRate());
        existing.setMonthCount(dto.getMonthCount());
        existing.setOpenDate(dto.getOpenDate());
        ProductRegistry updated=repository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteProductRegistry(Long id){
        if(!repository.existsById(id)){
            throw new IllegalArgumentException("ProductRegistry not found with id: "+ id);
        }
        repository.deleteById(id);
    }
    public boolean existsByClientAndProduct(Long clientId, Long productId){
        return repository.existsByClientIdAndProductId(clientId, productId);
    }
    public boolean hasClientDelayedPayments(Long clientId) {
        List<ProductRegistryDTO> clientProducts = getProductRegistriesByClientId(clientId);

        for (ProductRegistryDTO product : clientProducts) {
            List<PaymentRegistryDTO> payments = paymentRegistryService.getPaymentRegistriesByProductRegistryId(product.getId());
            boolean hasDelayedPayment = payments.stream()
                    .anyMatch(p -> Boolean.TRUE.equals(p.getExpired()));

            if (hasDelayedPayment) {
                return true;
            }
        }
        return false;
    }
    private ProductRegistryDTO convertToDTO(ProductRegistry productRegistry) {
   return ProductRegistryDTO.builder()
           .id(productRegistry.getId())
           .clientId(productRegistry.getClientId())
           .accountId(productRegistry.getAccountId())
           .productId(productRegistry.getProductId())
           .amount(productRegistry.getAmount())
           .interestRate(productRegistry.getInterestRate())
           .monthCount(productRegistry.getMonthCount())
           .openDate(productRegistry.getOpenDate())
   .build();
    }
}
