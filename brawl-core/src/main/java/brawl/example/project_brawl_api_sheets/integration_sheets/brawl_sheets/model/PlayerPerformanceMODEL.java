package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PlayerPerformanceMODEL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitas performances pertencem a um time.
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamMODEL team;

    // Muitas performances são de um jogador.
    @ManyToOne
    @JoinColumn(name = "player_tag")
    private PlayerMODEL player;

    // Muitas performances usam um brawler.
    @ManyToOne
    @JoinColumn(name = "brawler_name")
    private BrawlerMODEL brawler;
}