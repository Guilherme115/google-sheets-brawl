package brawl.example.brawl_tracker.service;

import brawl.example.brawl_tracker.entity.PlayerTagData;
import brawl.example.brawl_tracker.model.TeamMODEL;
import brawl.example.brawl_tracker.model.TeamPlayerTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    @Autowired
    private PlayerTagData tagData;
    //Aqui vamos pegar os dados do mongo vamos armazenar no team model e retornar a merda do team modeL(Chamdo de DTO)


    public List<TeamMODEL> getPlayersTagsANDnameTEAM() {
        List<TeamPlayerTag> dados = tagData.findAllBy();
        List<TeamMODEL> listTeamModel = new ArrayList<>();

        for (TeamPlayerTag time : dados) {
            TeamMODEL model = new TeamMODEL();
            model.setPlayersTags(time.getTags());
            model.setTeamName(time.getTeamName());

            listTeamModel.add(model);
        }

        return listTeamModel;
    }
}
