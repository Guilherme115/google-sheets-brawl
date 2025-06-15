package brawl.example.project_brawl_api_sheets.integration_sheets.dao;

import brawl.example.project_brawl_api_sheets.integration_sheets.entity.PlayerTagData;
import brawl.example.project_brawl_api_sheets.integration_sheets.entity.PlayerTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MogoReaderDAO {

    private PlayerTagData data;

    @Autowired
    public MogoReaderDAO(PlayerTagData data) {
        this.data = data;
    }


    public List<PlayerTagEntity> getAllPlayers() {
        return data.findAll();
    }

    public PlayerTagEntity getById(String id) {
        return data.findById(id).orElseThrow(()-> new IllegalArgumentException("Id not found"));
    }

    public List<PlayerTagEntity> getByTeamName(String teamName) {
        return data.findByTeamName(teamName);
    }

    public List<PlayerTagEntity> getByDiscordID(String discordID) {
        return data.findByDiscordID(discordID);
    }

    public List<PlayerTagEntity> getByTag(String tag) {
        return data.findByTagsContaining(tag);
    }

    public void save(PlayerTagEntity player) {
        data.save(player);
    }

    public void deleteById(String id) {
        data.deleteById(id);
    }

}

