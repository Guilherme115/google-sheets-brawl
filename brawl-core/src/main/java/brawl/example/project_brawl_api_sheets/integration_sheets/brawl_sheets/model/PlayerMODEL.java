package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "player_model")
public class PlayerMODEL {

    @Id
    private String tag;

    private String name;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> performances = new ArrayList<>();

    @ManyToMany(mappedBy = "players")
    private Set<TeamRegisterMODEL> registeredTeams = new HashSet<>();


}
