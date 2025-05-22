package brawl.example.project_brawl_api_sheets.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class BrawlRequestMODEL {
    @JsonProperty("items")
    List<BattleLogInfo> items;


    @Data
    @NoArgsConstructor
    public static class BattleLogInfo {
        String battleTime;
        private Battle battle;
        private Brawler brawler;

    }

    @Data
    @NoArgsConstructor
    public static class Battle {
        private String mode;
        private String type;
        private String result;
        private int duration;
        private List<Player> players;
    }

    @Data
    @NoArgsConstructor
    public static class Player {
        private String tag;
        private String name;
        private List<Brawler> brawlers;
    }

    @Data
    @NoArgsConstructor
    public static class Brawler {
        private int id;
        private String name;

    }
}
