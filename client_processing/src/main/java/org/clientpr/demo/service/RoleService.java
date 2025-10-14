package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clientpr.demo.model.User;
import org.clientpr.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final UserRepository userRepository;

    public boolean hasMasterRole(Long userId) {
        log.info("🔍 Checking MASTER role for user ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        boolean hasRole = user != null && "MASTER".equals(user.getUserRole());
        log.info("✅ MASTER role check for user {}: {}", userId, hasRole);
        return hasRole;
    }

    public boolean hasGrandEmployeeOrMasterRole(Long userId) {
        log.info("🔍 Checking GRAND_EMPLOYEE or MASTER role for user ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        boolean hasRole = user != null &&
                ("GRAND_EMPLOYEE".equals(user.getUserRole()) || "MASTER".equals(user.getUserRole()));
        log.info("✅ GRAND_EMPLOYEE/MASTER role check for user {}: {}", userId, hasRole);
        return hasRole;   }
    public String getUserRole(Long userId) {
        log.info("🔍 Getting role for user ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        String role = user != null ? user.getUserRole() : null;
        log.info("✅ User {} has role: {}", userId, role);
        return role;
    }
    public boolean hasUserRole(Long userId) {
        log.info("🔍 Checking USER role for user ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        boolean hasRole = user != null && "USER".equals(user.getUserRole());
        log.info("✅ USER role check for user {}: {}", userId, hasRole);
        return hasRole;
    }
}
