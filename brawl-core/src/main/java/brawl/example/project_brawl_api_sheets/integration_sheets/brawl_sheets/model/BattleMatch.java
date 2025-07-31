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

        // RELAÇÃO CORRIGIDA: Uma partida tem muitos times (escalações)
        @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MatchTeamMODEL> teams = new ArrayList<>();
}