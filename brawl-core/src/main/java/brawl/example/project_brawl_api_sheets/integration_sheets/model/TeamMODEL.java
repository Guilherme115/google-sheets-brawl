package brawl.example.project_brawl_api_sheets.integration_sheets.model;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;

@Data
@Component
public class TeamMODEL {
    private String teamName;
    private List<String> playersTags;
}
