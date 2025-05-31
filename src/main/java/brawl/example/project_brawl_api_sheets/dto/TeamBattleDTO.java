package brawl.example.project_brawl_api_sheets.dto;

import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
public class TeamBattleDTO {
    private String teamName;
    private List<BrawlRequestMODEL.BattleLogInfo> battles;
}
