package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamPlayerTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
/*
Serviço que valida se uma equipe é valida. (Obs: Os dados vao estar armazenado em MONGO DB)
 */
@Repository
public interface PlayerTagData extends MongoRepository<PlayerTagEntity, String> {
    boolean existsByTags(String tags);
    boolean existsByTeamName(String teamName);
    List<PlayerTagEntity> findByTeamName(String teamName);
    List<PlayerTagEntity> findByDiscordID(String discordID);
    List<PlayerTagEntity> findByTagsContaining(String tag);
    List<TeamPlayerTag> findAllBy();
}

