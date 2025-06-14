
package brawl.example.brawl_tracker.service;
import brawl.example.brawl_tracker.brawl_core.service.ActionsBotService;
import brawl.example.brawl_tracker.brawl_core.service.PlayerTagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import brawl.example.brawl_tracker.brawl_core.entity.PlayerTagData;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ActionsBotServiceTest {
   @Mock
   PlayerTagData playerTagData;
   @Mock
   PlayerTagService tagService;
   @InjectMocks
   ActionsBotService service;

    @BeforeEach
    public void setup() {
        AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Start bot, Primary test")
    void inicializateFlow() {
        String id = "user123";
        String response = service.inicializateFlow(id);
        assertEquals("Allright, let's get stared. Please insert name of Team", response);
        assertEquals(ActionsBotService.UserState.TEAMNAME, service.getFlowState().get(id));
    }

    @Test

    @DisplayName("Verify if, with a name that already exists in the database, the method returns as expected")
    void mainFlow_TeamNameAlreayRegister() {
        String id = "UserService";
        String teamName = "TeamName321";

        Mockito.when(playerTagData.existsByTeamName(teamName)).thenReturn(true);
        service.inicializateFlow(id);
        String response = service.mainFlow(id, teamName);
        assertEquals("Team name already registered",response);


    }

    @Test
    @DisplayName("Verify if method return exactaly like as expected")
    void mainFlow_QUANTITY_TAGS_VALID() {
        String id = "user123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);
        String response = service.mainFlow(id, "3");
        assertEquals("Please insert TAG of PLAYER #1", response);

    }

    @Test

    void mainFlow_QUANTITY_TAGS_INVALID() {
        String id = "user123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id, "-3");
        assertNotEquals("Please insert TAG of PLAYER #1", response);
    }


    @Test
    void mainFlow_INVALIDVALUE() {
        String id = "User123";
        service.getFlowState().put(id , ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id, "text");
        assertEquals("Please insert valid Number", response);

    }

    @Test

    void mainFlow_MAXIMUM_LIMIT() {
        String id = "User123";
        service.getFlowState().put(id, ActionsBotService.UserState.TAG_QUANTITY);

        String response = service.mainFlow(id,"5");
        assertEquals("Quantity of tags invalid",response);

    }
}
