package ru.kata.spring.boot_security.demo.configs;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(SuccessUserHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (role.equals("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        } else if (role.equals("ROLE_USER")) {
            response.sendRedirect("/user");
        } else {
            response.sendRedirect("/");
        }
    }
}