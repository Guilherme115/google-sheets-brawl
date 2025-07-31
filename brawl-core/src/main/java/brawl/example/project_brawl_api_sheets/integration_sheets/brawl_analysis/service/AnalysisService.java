package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.GeneralTeamInfo;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BattleMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor // Lombok para injeção de dependência via construtor
public class AnalysisService {

    // A dependência correta agora é o BattleMatchRepository
    private final BattleMatchRepository battleMatchRepository;

    /**
     * Calcula e retorna as informações gerais de performance de um time.
     * @param teamName O nome do time a ser analisado.
     * @return Um objeto GeneralTeamInfo com as estatísticas. Nunca retorna nulo.
     */
    public GeneralTeamInfo generalTeamInfo(String teamName) {
        GeneralTeamInfo teamInfo = new GeneralTeamInfo();
        teamInfo.setTeamName("test");
       return teamInfo;

    }

}