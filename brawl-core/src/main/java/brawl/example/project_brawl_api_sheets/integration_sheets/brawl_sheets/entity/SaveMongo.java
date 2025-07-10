package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
/*
- O save mongo é o serviço responsável
 */
public class SaveMongo {
    @Autowired
    private final PlayerTagData repository;
/*
Está classe está em desuso provavelmente devido a outras formas de salvar no mongo de dados. É necessario refatorar o codigo.
 */
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

