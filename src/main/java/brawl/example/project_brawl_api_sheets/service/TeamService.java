package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.entity.PlayerTagData;
import brawl.example.project_brawl_api_sheets.model.TeamMODEL;
import brawl.example.project_brawl_api_sheets.model.TeamPlayerTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    @Autowired
    private PlayerTagData tagData;
    //Aqui vamos pegar os dados do mongo vamos armazenar no team model e retornar a merda do team modeL(Chamdo de DTO)


    public List<TeamMODEL> getPlayerInfo() {
        List<TeamPlayerTag> dados = tagData.findAllBy();
        TeamMODEL model = new TeamMODEL();
        List<TeamMODEL> listTeamModel = new ArrayList<>();
        for (TeamPlayerTag time : dados) {
            model.setPlayersTags(time.getTags());
            model.setTeamName(time.getTeamName());

            listTeamModel.add(model);
        }

        return listTeamModel;
    }
}
