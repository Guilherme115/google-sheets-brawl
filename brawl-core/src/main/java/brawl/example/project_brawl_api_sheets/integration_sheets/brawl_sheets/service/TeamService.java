package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerTagRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamWithPlayersRelationDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.util.intern_structure.TeamPlayerTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private PlayerTagRepository tagData;


    public List<TeamWithPlayersRelationDTO> getPlayersTagsAndNameTeam() {

        List<TeamPlayerTag> dadosDoBanco = tagData.findAllBy();


        return dadosDoBanco.stream()
                .map(timeDoBanco -> {
                    TeamWithPlayersRelationDTO model = new TeamWithPlayersRelationDTO();
                    model.setTeamName(timeDoBanco.getTeamName());
                    model.setPlayersTags(timeDoBanco.getTags());
                    return model;
                })
                .collect(Collectors.toList());
    }
}