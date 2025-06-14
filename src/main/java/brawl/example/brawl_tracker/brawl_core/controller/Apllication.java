package brawl.example.brawl_tracker.controller;

import brawl.example.brawl_tracker.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> sync() {
        try {
            syncService.sync();
            return ResponseEntity.ok("Sync triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Sync failed: " + e.getMessage());
        }
    }
}





