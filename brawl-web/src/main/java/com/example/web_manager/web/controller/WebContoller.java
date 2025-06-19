package com.example.web_manager.web.controller;

import com.example.web_manager.web.service.InviteBotImplement;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bot")
public class WebContoller {

    private final InviteBotImplement botImplement;

    @Autowired
    public WebContoller(InviteBotImplement botImplement) {
        this.botImplement = botImplement;
    }

    @GetMapping("/invite")
    public ResponseEntity<Map<String, String>> getInvite() {
        String inviteLink = botImplement.GenrateLink();

        return ResponseEntity.ok(Map.of("invite_url", inviteLink));
    }
}