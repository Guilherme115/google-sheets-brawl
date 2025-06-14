package brawl.example.brawl_tracker.model;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
public class TeamMODEL {
    private String teamName;
    private List<String> playersTags;
}
