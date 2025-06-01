package brawl.example.project_brawl_api_sheets.controller;

import brawl.example.project_brawl_api_sheets.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class Apllication {

    private final SyncService syncService;


   @Autowired
    public Apllication(SyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/sync")
    public String sync() {
        return syncService.Sync();
    }
}







