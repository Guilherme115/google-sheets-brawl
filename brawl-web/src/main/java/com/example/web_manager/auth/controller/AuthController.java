package com.example.web_manager.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


//Aqui é depoois que ele logou?
        @GetMapping("/success")
        public String success(@AuthenticationPrincipal OAuth2User user) {
            return "Usuário logado com sucesso: " + user.getAttribute("username") + " (ID: " + user.getAttribute("id") + ")";
        }
//Aqui faz o login
        @GetMapping("/me")
        public OAuth2User me(@AuthenticationPrincipal OAuth2User user) {
            return user;
        }
    }
/*
- Aqui vamos receber o login do discord, iremos disponibilizar o avatar pro front

 */
