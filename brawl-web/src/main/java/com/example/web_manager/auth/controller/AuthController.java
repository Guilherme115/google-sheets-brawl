package com.example.web_manager.auth.controller;

import com.example.web_manager.auth.dto.DiscordUserResponse;
import com.example.web_manager.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/success")
    public void success(@AuthenticationPrincipal OAuth2User user, HttpServletResponse response) throws IOException {
        if (user == null) {
            response.sendRedirect("http://localhost:4200/login?error=unauthorized");
            return;
        }

        String token = jwtUtil.generateToken(user);
        String id = user.getAttribute("id");
        String username = user.getAttribute("username");
        String avatar = user.getAttribute("avatar");

        String redirectUrl = String.format(
                "http://localhost:4200/login-success?token=%s&id=%s&username=%s&avatar=%s",
                token, id, username, avatar
        );

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/me")
    public DiscordUserResponse me(@AuthenticationPrincipal OAuth2User user) {

        return DiscordUserResponse.fromAuth2User(user);
    }
}
