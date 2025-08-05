package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan(basePackages = [
        "brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service",
        "brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository"
])
class SyncServiceTestConfig {}
