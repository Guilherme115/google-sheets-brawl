package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class BattleLogReceiveDTO {

    @JsonProperty("items")
    @Id
    private long id;
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
        private String mode;
        private String id;
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
