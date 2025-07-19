package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto;

import lombok.*;
/*
-Está classe é um model a parte. Nesta classe é onde há o primeiro contato com o JSON, primeiramente esta classe deveria SER um DTO
-Se eu quiser armazenar informações relacionados a verificação do PlayerValid eu deveria criar um MODEL a parte;
 */
@Data
public class PlayerValidDTO {

    private String name;
    private Integer trophies;
    private boolean isQualifiedFromChampionshipChallenge;
}
