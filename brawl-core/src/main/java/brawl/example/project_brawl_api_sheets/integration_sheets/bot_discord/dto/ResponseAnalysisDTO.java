package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResponseAnalysisDTO {
    @NotNull
    private String brawlerName;
    @NotNull
    private Integer UsageCount;
}
