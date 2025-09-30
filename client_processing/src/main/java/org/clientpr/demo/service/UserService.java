package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.aop.annotations.LogDatasourceError;
import org.clientpr.demo.model.User;
import org.clientpr.demo.model.dto.UserDTO;
import org.clientpr.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @LogDatasourceError(type = "ERROR")
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByLogin(userDTO.getLogin())) {
            throw new IllegalArgumentException("Login already exists: " + userDTO.getLogin());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        User user = User.builder(
                userDTO.getLogin(),
                userDTO.getPassword(), // this needs to be hash, but well do that in JWT lesson
                userDTO.getEmail()
                ).build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public UserDTO getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with login: " + login));
    }

    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!existingUser.getLogin().equals(userDTO.getLogin()) &&
                userRepository.existsByLogin(userDTO.getLogin())) {
            throw new IllegalArgumentException("Login already exists: " + userDTO.getLogin());
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        existingUser.setLogin(userDTO.getLogin());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setEmail(userDTO.getEmail());

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
    }
}

