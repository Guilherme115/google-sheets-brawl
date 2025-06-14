package brawl.example.brawl_tracker.dto;

import brawl.example.brawl_tracker.model.BrawlRequestMODEL;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class TeamBattleDTO {
    private String teamName;
    private List<BrawlRequestMODEL.BattleLogInfo> battles;
}
