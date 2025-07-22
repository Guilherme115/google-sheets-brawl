package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerTagMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.util.intern_structure.TeamPlayerTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
/*
Serviço que valida se uma equipe é valida. (Obs: Os dados vao estar armazenado em MONGO DB)
 */
@Repository
public interface PlayerTagRepository extends MongoRepository<PlayerTagMODEL, String> {
    boolean existsByTags(String tags);

    boolean existsByTeamName(String teamName);

    List<PlayerTagMODEL> findByTeamName(String teamName);

    List<PlayerTagMODEL> findByDiscordID(String discordID);

    List<PlayerTagMODEL> findByTagsContaining(String tag);

    List<TeamPlayerTag> findAllBy();
}

