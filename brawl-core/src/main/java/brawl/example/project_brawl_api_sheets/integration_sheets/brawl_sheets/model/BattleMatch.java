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
@Table(name = "battle_match")
public class BattleMatch {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long internalId;

        private LocalDateTime battleTime;
        private String map;
        private String mode;
        private String type;
        private String result;
        private int duration;

        @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MatchTeamMODEL> teams = new ArrayList<>();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "match_set_id")
        private MatchSet matchSet;
}