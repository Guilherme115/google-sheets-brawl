package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "player_performance")
public class PlayerPerformanceMODEL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamMODEL team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_tag", nullable = false)
    private PlayerMODEL player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brawler_name", nullable = false)
    private BrawlerMODEL brawler;
}