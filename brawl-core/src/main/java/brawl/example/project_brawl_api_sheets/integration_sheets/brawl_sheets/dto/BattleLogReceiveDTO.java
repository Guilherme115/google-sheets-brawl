package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class BattleLogReceiveDTO {


    @JsonProperty("items")
    private List<BattleLogInfo> items;


    @Data
    @NoArgsConstructor
    public static class BattleLogInfo {
        private String battleTime;
        private Battle battle;
    }

    @Data
    @NoArgsConstructor
    public static class Battle {
        private String id;
        private String mode;
        private String type;
        private String result;
        private int duration;
        private List<List<Player>> teams;


    }

    @Data
    @NoArgsConstructor
    public static class Player {
        private String tag;
        private String name;
        private Brawler brawler;
    }

    @Data
    @NoArgsConstructor
    public static class Brawler {
        private String name;
    }
}