package com.example.web_manager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

@AllArgsConstructor
@NoArgsConstructor
@Component
@Data
public class DiscordUserResponse {
    private String id;
    private String username;
    private String avatarUrl;
    private String discriminator;

    public static DiscordUserResponse fromAuth2User (OAuth2User user) {
        String id = user.getAttribute("id");
        String avatarHash = user.getAttribute("avatar");
        String avatarUrl = String.format("https://cdn.discordapp.com/avatars/%s/%s", id, avatarHash);

        return new DiscordUserResponse(
                id,
                user.getAttribute("username"),
                avatarUrl,
                user.getAttribute("discriminator"));


    }

}
