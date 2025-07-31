package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantDTO {
    private String name;
    @JsonProperty("img")
    private String logoUrl;
    private List<PlayerDTO> players;
}