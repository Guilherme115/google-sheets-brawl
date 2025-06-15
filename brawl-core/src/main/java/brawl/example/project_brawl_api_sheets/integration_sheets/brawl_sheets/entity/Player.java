package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
/*
Aqui informações relacionadas a Players
 */
public class Player  {
    @Id
    @GeneratedValue
    private long id;

    private String tag;
    private String name;
    private String brawler;
    private boolean isEnemy;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
