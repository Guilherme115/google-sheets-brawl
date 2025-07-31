package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamRegisterMODEL, Long> {

    // Busca um time pelo nome (usado no DataSeeder e AnalysisService)
    Optional<TeamRegisterMODEL> findByName(String name);

    // Verifica se um time com um nome já existe (usado no RegistrationService)
    boolean existsByName(String name);
}