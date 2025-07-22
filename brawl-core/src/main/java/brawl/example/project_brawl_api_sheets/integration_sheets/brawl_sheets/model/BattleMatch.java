package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "battle_match")
public class BattleMatch {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long internalId;

        private String battleTime;
        private String mode;
        private String type;
        private String result;
        private int duration;

        // UMA Batalha tem MUITOS Times
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
                name = "battle_teams", // Nome da tabela de junção
                joinColumns = @JoinColumn(name = "battle_match_id"), // Coluna que referencia BattleMatch
                inverseJoinColumns = @JoinColumn(name = "team_model_id") // Coluna que referencia TeamMODEL
        )
        private List<TeamMODEL> teams = new ArrayList<>();
}