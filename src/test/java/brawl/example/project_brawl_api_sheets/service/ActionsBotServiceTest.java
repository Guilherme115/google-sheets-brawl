package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.entity.PlayerTagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActionsBotServiceTest {


    @Test
    @DisplayName("Start bot, Primary test")
    void inicializateFlow() {
        ActionsBotService service = new ActionsBotService();
        String id = "user123";
        String response = service.inicializateFlow(id);
        assertEquals("Allright, let's get stared. Please insert name of Team", response);
        assertEquals(ActionsBotService.UserState.TEAMNAME, service.getFlowState().get(id));
    }

    @Test
    @BeforeEach
    void mainFlow_NAMETEAM() {
        ActionsBotService service = new ActionsBotService();
        String id = "UserService";
        String teamName = "TeamName";
        String resp1 = service.inicializateFlow(id);
        assertEquals("Allright, let's get stared. Please insert name of Team", resp1);
        String resp2 = service.mainFlow(id, teamName);
        assertEquals("Please insert to quantity of tags", resp2);
        assertEquals(ActionsBotService.UserState.TAG_QUANTITY, service.getFlowState().get(id));

    }

    @Test
    @DisplayName("Verify if method return exactaly like as expected  ")
    void mainFlow_QUANTITY_TAGS_VALID() {
        ActionsBotService service = new ActionsBotService();
        String id = "user123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id, "3");
        assertEquals("Please insert TAG of PLAYER #1", response);

    }

    @Test
    @BeforeEach
    void mainFlow_QUANTITY_TAGS_INVALID() {
        ActionsBotService service = new ActionsBotService();
        String id = "user123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id, "-3");
        assertNotEquals("Please insert TAG of PLAYER #1", response);
    }


    @Test
    @BeforeEach
    void mainFlow_INVALIDVALUE() {
        ActionsBotService service = new ActionsBotService();
        String id = "User123";
        service.getFlowState().put(id , ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id, "text");
        assertEquals("Please insert valid Number", response);

    }

    @Test
    @BeforeEach
    void mainFlow_MAXIMUM_LIMIT() {
        ActionsBotService service = new ActionsBotService();
        String id = "User123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id,"5");
        assertEquals("Quantity of tags invalid",response);

    }
}