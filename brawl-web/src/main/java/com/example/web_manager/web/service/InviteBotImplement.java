package com.example.web_manager.web.service;

public class InviteBotImplement implements InviteBotService{
    @Override
    public String GenrateLink() {

        String clientId = "1376200774144626728";
        String permissions = "2147577472"; //
        String inviteLink = "https://discord.com/oauth2/authorize?client_id=" + clientId
                + "&scope=bot&permissions=" + permissions;

        return inviteLink;
    }
}
