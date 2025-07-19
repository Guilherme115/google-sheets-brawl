package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
public class BattleMatch {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long internalId; //

        private String battleTime;
        private String mode;
        private String type;
        private String result;
        private int duration;

        @OneToMany(mappedBy = "battleMatch", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<TeamMODEL> teams;
    }

