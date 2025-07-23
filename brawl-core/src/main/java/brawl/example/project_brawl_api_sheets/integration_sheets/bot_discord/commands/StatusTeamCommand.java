package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.commands;

;
import brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.view.AnalysisEmbedService;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.GeneralTeamInfo;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service.AnalysisService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class StatusTeamCommand implements ICommand {

    private final AnalysisService analysisService;
    private final AnalysisEmbedService embedService;

    public StatusTeamCommand(AnalysisService analysisService, AnalysisEmbedService embedService) {
        this.analysisService = analysisService;
        this.embedService = embedService;
    }

    @Override
    public String getName() {
        return "status-team";
    }

    @Override
    public String getDescription() {
        return "Mostra as estatísticas de desempenho de um time.";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
                new OptionData(OptionType.STRING, "team_name", "O nome do time para analisar", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String teamName = Objects.requireNonNull(event.getOption("team_name")).getAsString();

        event.deferReply().queue();

        GeneralTeamInfo teamInfo = analysisService.generalTeamInfo(teamName);

        MessageEmbed embed = embedService.createTeamAnalysisEmbed(teamInfo);

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}