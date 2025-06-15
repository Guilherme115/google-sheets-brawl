package brawl.example.brawl_tracker.brawl_core.dto;

import brawl.example.brawl_tracker.brawl_core.model.BrawlRequestMODEL;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class TeamBattleDTO {
    private String teamName;
    private List<BrawlRequestMODEL.BattleLogInfo> battles;
}
