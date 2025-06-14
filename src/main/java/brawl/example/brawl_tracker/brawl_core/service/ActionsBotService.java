package brawl.example.brawl_tracker.brawl_core.service;

import brawl.example.brawl_tracker.brawl_core.entity.PlayerTagData;
import brawl.example.brawl_tracker.brawl_core.entity.PlayerTagEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Getter
public class ActionsBotService {
    @Getter
    private final Map<String, UserState> flowState = new HashMap<>();

    /*
    Aqui ele armazena as informações e coloca no MONGODB
     */
    private final Map<String, PlayerTagEntity> parcialStorage = new HashMap<>();
    /*
    RECEBE ATÉ 4 TAGS.
     */
    private final Map<String, List<String>> tagsReceive = new HashMap<>(4);
    /*
    Quantidae de tags.
     */
    private final Map<String, Integer> quantityTagsMap = new HashMap<>();

    private final PlayerTagData playerTagData;
    private final  PlayerTagService tagService;

    @Autowired
    public ActionsBotService(PlayerTagData playerTagData, PlayerTagService tagService) {
        this.playerTagData = playerTagData;
        this.tagService = tagService;
    }






    public String inicializateFlow(String userID) {
        flowState.put(userID, UserState.TEAMNAME);

        parcialStorage.put(userID, new PlayerTagEntity());

        return "Allright, let's get stared. Please insert name of Team";
    }


    public String mainFlow(String userID, String mensageReceived) {

        UserState state = flowState.get(userID);
        PlayerTagEntity entity = parcialStorage.get(userID);

        switch (String.valueOf(state)) {
            case "TEAMNAME":
                if (playerTagData.existsByTeamName(mensageReceived)) return "Team name already registered";

                entity.setTeamName(mensageReceived);
                flowState.put(userID, UserState.TAG_QUANTITY);

                return "Please insert to quantity of tags";


            case "TAG_QUANTITY":
                System.out.println("Mensagem recebida: '" + mensageReceived + "'");

                String cleanInput = mensageReceived.trim();

                if (cleanInput.isEmpty()) {
                    return "You must insert a number, not nothing 🤦‍♂️ mensage received :" + " -> " + cleanInput;
                }
                try {
                    Integer quantityTags = Integer.valueOf(mensageReceived.trim());
                    System.out.println(quantityTags);
                    if (quantityTags < 3 || quantityTags > 4) {

                        return "Quantity of tags invalid";
                    }
                    quantityTagsMap.put(userID, quantityTags);
                    flowState.put(userID, UserState.TAG_INPUT);

                    return "Please insert TAG of PLAYER #1";

                } catch (NumberFormatException e) {
                    return "Please insert valid Number";
                }


            case "TAG_INPUT":
                List<String> listTags = tagsReceive.computeIfAbsent(userID, k -> new ArrayList<>());
                if (tagService.isPlayerTagisValid(mensageReceived)) {
                    String name = tagService.NameOfPlayer(mensageReceived);
                    if (playerTagData.existsByTags(mensageReceived))
                        return "The player" + name + "Already registered" + "Use /Timescasted to access the list of teams have already been registered or Try again";

                    listTags.add(mensageReceived);

                    int quantity = quantityTagsMap.getOrDefault(userID, 0);
                    if (listTags.size() < quantity) {
                        return "Very nice player" + name + "registered , insert Player tag number # " + (listTags.size() + 1);
                    } else {
                        flowState.put(userID, UserState.FINISHED);

                        PlayerTagEntity finalEntity = parcialStorage.get(userID);
                        finalEntity.setTags(listTags);
                        finalEntity.setDiscordID(userID);
                        playerTagData.save(finalEntity);
                        System.out.println("Entidade salva com sucesso:" + finalEntity);

                        return "Thank you for contributing. All tags registered!";
                    }
                } else {

                    return "Bro, this player not exist or you are loving registering a random player";
                }

            case "FINISHED":
                return "Thank you for Contributing";

            default:
                return "Flow Stopped, try again";

        }
    }

    public enum UserState {TEAMNAME, TAG_INPUT, FINISHED, TAG_QUANTITY}

}
