package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    private String name;
    private String gamerTag;
}