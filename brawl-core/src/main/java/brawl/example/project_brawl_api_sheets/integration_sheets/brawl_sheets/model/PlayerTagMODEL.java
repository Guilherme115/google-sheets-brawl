package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@NoArgsConstructor
@Document(collection = "playersTags")
@Data
public class PlayerTagMODEL {

    @Id
    private String id;
    private String discordID;
    private String teamName;
    private List<String> tags;


}

