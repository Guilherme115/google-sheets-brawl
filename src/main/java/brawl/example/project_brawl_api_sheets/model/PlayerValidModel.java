package brawl.example.project_brawl_api_sheets.model;

import lombok.*;

@Data
public class PlayerValidModel {

    @NonNull
    private String name;
    private Integer trophies;
    private boolean isQualifiedFromChampionshipChallenge;
}
