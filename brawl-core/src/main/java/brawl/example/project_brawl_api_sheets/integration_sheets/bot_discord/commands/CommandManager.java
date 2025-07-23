package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.commands;// package ...bot_discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommandManager {

    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandManager(Collection<ICommand> commands) {
        for (ICommand command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void handle(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        ICommand command = commands.get(commandName);
        if (command != null) {
            command.execute(event);
        } else {
            event.reply("Comando desconhecido.").setEphemeral(true).queue();
        }
    }

    public Collection<ICommand> getAllCommands() {
        return commands.values();
    }
}