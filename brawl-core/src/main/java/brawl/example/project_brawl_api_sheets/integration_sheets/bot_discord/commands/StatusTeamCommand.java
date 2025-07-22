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

    // Precisamos definir as opções que o comando aceita
    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
                new OptionData(OptionType.STRING, "team_name", "O nome do time para analisar", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Pega o nome do time que o usuário digitou na opção do comando
        String teamName = Objects.requireNonNull(event.getOption("team_name")).getAsString();

        event.deferReply().queue(); // Informa ao Discord que a resposta pode demorar um pouco

        // 1. Pega os dados brutos do serviço de análise
        GeneralTeamInfo teamInfo = analysisService.generalTeamInfo(teamName);

        // 2. Transforma os dados em um Embed bonito
        MessageEmbed embed = embedService.createTeamAnalysisEmbed(teamInfo);

        // 3. Envia o embed como resposta
        event.getHook().sendMessageEmbeds(embed).queue();
    }
}