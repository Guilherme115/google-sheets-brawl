package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.commands;// package ...bot_discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public interface ICommand {
    String getName();
    String getDescription();
    void execute(SlashCommandInteractionEvent event);
    default List<OptionData> getOptions() {
        return Collections.emptyList(); //
    }
}