package brawl.example.brawl_tracker.brawl_core.model;

import lombok.*;

@Data
public class PlayerValidModel {

    @NonNull
    private String name;
    private Integer trophies;
    private boolean isQualifiedFromChampionshipChallenge;
}
