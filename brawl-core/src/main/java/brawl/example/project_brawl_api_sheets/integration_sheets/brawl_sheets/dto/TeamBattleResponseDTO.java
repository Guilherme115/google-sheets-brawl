package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class TeamBattleResponseDTO {
    private String teamName;
    private List<BattleLogReceiveDTO.BattleLogInfo> battles;
}
