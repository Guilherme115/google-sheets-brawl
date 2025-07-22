package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "team_model")
public class TeamMODEL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameTeam;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    // UM Time pode ter MUITAS Batalhas
    // "mappedBy = "teams"" diz ao Hibernate: "A configuração desta relação já foi feita
    // no campo 'teams' da classe BattleMatch. Apenas use aquela configuração."
    @ManyToMany(mappedBy = "teams")
    private List<BattleMatch> battles = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> players = new ArrayList<>();
}