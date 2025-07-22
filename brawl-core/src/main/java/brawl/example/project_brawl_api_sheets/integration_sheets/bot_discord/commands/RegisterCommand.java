package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.commands;// package ...bot_discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

@Component
public class RegisterCommand implements ICommand {

    @Override
    public String getName() {
        return "register-team";
    }

    @Override
    public String getDescription() {
        return "Registra um novo time com 3 ou 4 jogadores.";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextInput teamName = TextInput.create("team-name", "Nome do Time", TextInputStyle.SHORT)
                .setPlaceholder("Insira o nome do time")
                .setRequired(true)
                .build();

        TextInput player1 = TextInput.create("player-1", "Tag do Jogador #1", TextInputStyle.SHORT)
                .setPlaceholder("#ABC12345")
                .setRequired(true)
                .build();

        TextInput player2 = TextInput.create("player-2", "Tag do Jogador #2", TextInputStyle.SHORT)
                .setPlaceholder("#ABC12345")
                .setRequired(true)
                .build();

        TextInput player3 = TextInput.create("player-3", "Tag do Jogador #3", TextInputStyle.SHORT)
                .setPlaceholder("#ABC12345")
                .setRequired(true)
                .build();

        TextInput player4 = TextInput.create("player-4", "Tag do Jogador #4 (Opcional)", TextInputStyle.SHORT)
                .setPlaceholder("#ABC12345")
                .setRequired(false)
                .build();

        Modal modal = Modal.create("registration-modal", "Registro de Time")
                .addComponents(ActionRow.of(teamName), ActionRow.of(player1), ActionRow.of(player2), ActionRow.of(player3), ActionRow.of(player4))
                .build();

        event.replyModal(modal).queue();
    }
}