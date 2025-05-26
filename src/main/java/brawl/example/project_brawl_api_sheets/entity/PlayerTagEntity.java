package brawl.example.project_brawl_api_sheets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor

public class PlayerTagEntity {
    @Id
    private String id;



    private String DiscordID;
    private String TeamName;
    private List<String> tags;

}

