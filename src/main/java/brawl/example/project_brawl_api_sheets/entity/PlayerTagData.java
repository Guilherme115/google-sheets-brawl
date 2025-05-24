package brawl.example.project_brawl_api_sheets.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTagData extends JpaRepository <PlayerTagEntity, Long> {
    boolean existsByTags(String tags);

}
