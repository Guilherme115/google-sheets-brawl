package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.view;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.GeneralTeamInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.time.Instant;

@Service
public class AnalysisEmbedService {

    public MessageEmbed createTeamAnalysisEmbed(GeneralTeamInfo info) {
        if (info == null) {
            return new EmbedBuilder()
                    .setTitle("❌ Erro na Análise")
                    .setDescription("O time solicitado não foi encontrado em nossa base de dados.")
                    .setColor(Color.RED)
                    .build();
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🏆 Análise de Desempenho: " + info.getTeamName());

        Color embedColor = info.getWinrate() >= 50 ? new Color(0x4CAF50) : new Color(0xF44336);
        embed.setColor(embedColor);

        long derrotas = info.getNumberOfMatches() - info.getVictors();
        String winrateFormatado = String.format("%.2f%%", info.getWinrate());
        embed.setDescription("Confira as estatísticas de batalha para o time **" + info.getTeamName() + "**.");

        embed.setThumbnail("https://i.imgur.com/gJ4dY6E.png"); // Exemplo de um ícone de troféu

        embed.addField("Partidas Totais", String.valueOf(info.getNumberOfMatches()), true); // 'true' para deixar em linha
        embed.addField("Vitórias", String.valueOf(info.getVictors()), true);
        embed.addField("Derrotas", String.valueOf(derrotas), true);

        embed.addField("📊 Taxa de Vitória (Win Rate)", "**" + winrateFormatado + "**", false);

        embed.setFooter("Análise gerada pelo Bot Brawler", null); // O segundo parâmetro pode ser um ícone
        embed.setTimestamp(Instant.now());

        return embed.build();
    }
}