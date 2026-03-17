package com.financetracker.service;

import com.financetracker.dto.AuthDto;
import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(User.Role.USER)
                .monthlyIncome(req.getMonthlyIncome() != null ? req.getMonthlyIncome() : java.math.BigDecimal.ZERO)
                .targetExpenses(req.getTargetExpenses() != null ? req.getTargetExpenses() : java.math.BigDecimal.ZERO)
                .savingsTarget(req.getSavingsTarget() != null ? req.getSavingsTarget() : java.math.BigDecimal.ZERO)
                .currency(req.getCurrency() != null ? req.getCurrency() : "USD")
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCurrency());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCurrency());
    }

    @Transactional
    public User updateProfile(String email, AuthDto.UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getName() != null) user.setName(req.getName());
        if (req.getMonthlyIncome() != null) user.setMonthlyIncome(req.getMonthlyIncome());
        if (req.getTargetExpenses() != null) user.setTargetExpenses(req.getTargetExpenses());
        if (req.getSavingsTarget() != null) user.setSavingsTarget(req.getSavingsTarget());
        if (req.getCurrency() != null) user.setCurrency(req.getCurrency());

        return userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
