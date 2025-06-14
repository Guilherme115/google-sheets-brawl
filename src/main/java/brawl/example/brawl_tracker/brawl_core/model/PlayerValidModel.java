package brawl.example.brawl_tracker.model;

import lombok.*;

@Data
public class PlayerValidModel {

    @NonNull
    private String name;
    private Integer trophies;
    private boolean isQualifiedFromChampionshipChallenge;
}
