package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GeneralTeamInfo {

    private String teamName;
    private Integer NumberOfMatches;
    private Long victors;
    private Double winrate;

}
