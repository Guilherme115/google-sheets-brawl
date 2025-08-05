package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model;

import lombok.Getter;

@Getter
public enum Region {
    SA("América do Sul"),
    NA("América do Norte"),
    EU("Europa"), 
    EA("Leste Asiático");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }
}