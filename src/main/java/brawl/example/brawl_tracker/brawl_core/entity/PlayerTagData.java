package brawl.example.brawl_tracker.entity;

import brawl.example.brawl_tracker.model.TeamPlayerTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PlayerTagData extends MongoRepository<PlayerTagEntity, String> {
    boolean existsByTags(String tags);
    boolean existsByTeamName(String teamName);
    List<PlayerTagEntity> findByTeamName(String teamName);
    List<PlayerTagEntity> findByDiscordID(String discordID);
    List<PlayerTagEntity> findByTagsContaining(String tag);
    List<TeamPlayerTag> findAllBy();
}

