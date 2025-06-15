package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
/*
Serviço para o JSON dessesiarlizar corretamente.
 */
public class Team extends Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int index;
    private boolean isMyTeam;

    @ManyToOne
    @JoinColumn (name = "battle_id")
    private Battle battle;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;


}
