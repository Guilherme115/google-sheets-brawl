package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "player_model")
public class PlayerMODEL {

    @Id
    private String tag; // A tag é o identificador único, não precisamos de um ID gerado.

    private String name;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> performances = new ArrayList<>();
}