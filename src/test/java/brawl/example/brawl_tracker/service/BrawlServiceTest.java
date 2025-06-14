package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import brawl.example.project_brawl_api_sheets.model.TeamMODEL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import okio.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.lang.*;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BrawlServiceTest {
    //@Autowired
//    public BrawlService(RestTemplate restTemplate, ObjectMapper mapper) {
//        this.restTemplate = restTemplate;
//        this.mapper = mapper;
//    }
    @InjectMocks
    private BrawlService service;

    @Mock
    ObjectMapper mapper;
    File file = new File("Util/TestBrawlModel.json");

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Test_GetTeams() throws IOException {
        List<String> playerTags = List.of("PlayerTag123", "PlayerTag321", "PlayerTag456");
        String mainTag = playerTags.get(0);

        TeamMODEL model = new TeamMODEL();
        model.setPlayersTags(playerTags);
        model.setTeamName("Test Team");

        ObjectMapper realMapper = new ObjectMapper();
        String jsonContent = new String(Files.readAllBytes(file.toPath()));
        BrawlRequestMODEL logInfo = realMapper.readValue(file, BrawlRequestMODEL.class);
        ResponseEntity<String> fakeResponse = ResponseEntity.ok(jsonContent);

        Mockito.
                when(mapper.readValue(Mockito.any(String.class),
                        Mockito.eq(BrawlRequestMODEL.class)))
                .thenReturn(logInfo);


        Mockito.
                when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        Mockito.eq(String.class)
                )).thenReturn(fakeResponse);


        TeamBattleDTO response = service.getTeams(mainTag, model);
        assertNotNull(response);
        assertEquals("Test Team", response.getTeamName());


    }

    @Test
    public void Test_List_Empty() throws IOException {
        List<String> nullList = List.of("","");
        String mainTag = nullList.get(0);

        TeamMODEL model = new TeamMODEL();
        model.setTeamName("Time vazio");
        model.setPlayersTags(nullList);

        ObjectMapper realMapper = new ObjectMapper();
        String jsonContent = new String(Files.readAllBytes(file.toPath()));
        BrawlRequestMODEL logInfo = realMapper.readValue(file, BrawlRequestMODEL.class);
        ResponseEntity<String> fakeResponse = ResponseEntity.ok(jsonContent);

        Mockito.
                when(mapper.readValue(Mockito.any(String.class),
                        Mockito.eq(BrawlRequestMODEL.class)))
                .thenReturn(logInfo);


        Mockito.
                when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.any(HttpEntity.class),
                        Mockito.eq(String.class)
                )).thenReturn(fakeResponse);

        TeamBattleDTO teams = service.getTeams(mainTag, model);

        assertNull(teams,"A lista está vazia, e o objeto nao foi criado");


    }
}