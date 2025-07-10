package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class TeamBattleDTO {
    private String teamName;
    private List<BattleLog.BattleLogInfo> battles;
}
