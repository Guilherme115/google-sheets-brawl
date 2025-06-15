package brawl.example.project_brawl_api_sheets.integration_sheets.entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class SaveMongo {
    @Autowired
    private final PlayerTagData repository;

    public SaveMongo(PlayerTagData repository) {
        this.repository = repository;
    }

    public PlayerTagEntity salvarTeamsNoMongo (Map<List<String>, String> tagOfTeam) {

        PlayerTagEntity team = new PlayerTagEntity();

        for (Map.Entry<List<String>, String> entry : tagOfTeam.entrySet()) {
            List<String> tags = entry.getKey();
            String teamName = entry.getValue();

            team.setTeamName(teamName);
            team.setTags(tags);

            repository.save(team);
        }
        return team;
    }

    }

