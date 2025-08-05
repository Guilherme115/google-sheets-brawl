package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "brawler_model")
public class BrawlerMODEL {

    @Id
    private String name;

    @OneToMany(mappedBy = "brawler", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerPerformanceMODEL> performances = new ArrayList<>();
}