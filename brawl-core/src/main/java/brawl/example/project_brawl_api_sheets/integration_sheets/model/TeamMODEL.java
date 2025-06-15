<<<<<<<< HEAD:brawl-core/src/main/java/brawl/example/project_brawl_api_sheets/integration_sheets/model/TeamMODEL.java
package brawl.example.project_brawl_api_sheets.integration_sheets.model;
========
package brawl.example.brawl_tracker.brawl_core.model;
>>>>>>>> origin/main:src/main/java/brawl/example/brawl_tracker/brawl_core/model/TeamMODEL.java
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
public class TeamMODEL {
    private String teamName;
    private List<String> playersTags;
}
