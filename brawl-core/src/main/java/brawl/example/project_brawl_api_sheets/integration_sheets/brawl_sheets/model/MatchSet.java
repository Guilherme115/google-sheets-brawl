package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "match_set")
public class MatchSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamAName;
    private String teamBName;
    private String finalResult; // Ex: "2-1"
    private String winningTeamName;
    private LocalDateTime setStartTime;

    @OneToMany(mappedBy = "matchSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleMatch> battles = new ArrayList<>();
}