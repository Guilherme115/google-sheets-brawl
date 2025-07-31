package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "match_teams")
public class MatchTeamMODEL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameTeam; // Nome do time na partida (ex: "SUP E-sports" ou "Oponente")

    @Enumerated(EnumType.STRING)
    private TeamType teamType; // Se é MY_TEAM ou ENEMY_TEAM

    // RELAÇÃO CORRIGIDA: Uma escalação de time pertence a UMA partida
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_match_id", nullable = false)
    private BattleMatch battle;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> players = new ArrayList<>();
}