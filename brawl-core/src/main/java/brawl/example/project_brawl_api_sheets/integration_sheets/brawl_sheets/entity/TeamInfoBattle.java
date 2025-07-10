package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TeamInfoBattle {
    private String teamName;

    @OneToMany(mappedBy = "team")
    private List<Battle> battleList ;
}
