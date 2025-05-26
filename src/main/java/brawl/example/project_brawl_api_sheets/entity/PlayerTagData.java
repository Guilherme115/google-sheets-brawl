package brawl.example.project_brawl_api_sheets.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTagData extends MongoRepository<PlayerTagEntity, String> {
    boolean existsByTags(String tags);

}
