package brawl.example.project_brawl_api_sheets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class PlayerTagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String tags; // Armazena tags como "tag1,tag2,tag3"

    // Getter que transforma a string em lista
    public List<String> getTagsList() {
        return tags != null ? List.of(tags.split(",")) : List.of();
    }

    public void setTagsList(List<String> tagsList) {
        this.tags = String.join(",", tagsList);
    }
}

