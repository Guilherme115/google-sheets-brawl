
package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
public class TeamWithPlayersRelationDTO {
    private String teamName;
    private List<String> playersTags;
}
