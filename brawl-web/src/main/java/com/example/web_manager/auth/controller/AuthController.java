package com.example.web_manager.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> success(@AuthenticationPrincipal OAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuário não autenticado"));
        }

        String token = jwtUtil.generateToken(user);
        Map<String,Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
                "id", user.getAttribute("id"),
                "username", user.getAttribute("username"),
                "avatar", user.getAttribute("avatar")
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public DiscordUserResponse me(@AuthenticationPrincipal OAuth2User user) {

        return DiscordUserResponse.fromAuth2User(user);
    }
}
