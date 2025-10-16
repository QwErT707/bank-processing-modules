package org.clientpr.demo.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.clientpr.demo.model.User;
import org.clientpr.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void hasMasterRole_WithMasterUser_ShouldReturnTrue() {
        User masterUser = new User();
        masterUser.setUserRole("MASTER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(masterUser));

        boolean result = roleService.hasMasterRole(1L);

        assertTrue(result);
    }

    @Test
    void hasMasterRole_WithNonMasterUser_ShouldReturnFalse() {
        User regularUser = new User();
        regularUser.setUserRole("USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(regularUser));

        boolean result = roleService.hasMasterRole(1L);

        assertFalse(result);
    }

    @Test
    void hasGrandEmployeeOrMasterRole_WithGrandEmployee_ShouldReturnTrue() {
        User grandEmployee = new User();
        grandEmployee.setUserRole("GRAND_EMPLOYEE");
        when(userRepository.findById(1L)).thenReturn(Optional.of(grandEmployee));

        boolean result = roleService.hasGrandEmployeeOrMasterRole(1L);

        assertTrue(result);
    }

    @Test
    void getUserRole_WithExistingUser_ShouldReturnRole() {
        User user = new User();
        user.setUserRole("USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String role = roleService.getUserRole(1L);

        assertEquals("USER", role);
    }
}
