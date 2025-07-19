package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class TeamMODEL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameTeam;


    @Enumerated(EnumType.STRING)
    private TeamType teamType;


    @ManyToOne
    @JoinColumn(name = "battle_match_id")
    private BattleMatch battle;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> players;
}