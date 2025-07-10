package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.Battle;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.BrawlMatchesSave;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.TeamInfoBattle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrieveDataBase {


    private final BrawlMatchesSave saveRepository;

    @Autowired
    public RetrieveDataBase(BrawlMatchesSave saveRepository) {
        this.saveRepository = saveRepository;
    }

    public List<TeamInfoBattle> getAllMatches() {

        return saveRepository.findAll();
    }
}
