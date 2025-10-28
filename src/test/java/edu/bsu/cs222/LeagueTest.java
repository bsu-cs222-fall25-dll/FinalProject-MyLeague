package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.bsu.cs222.Position.*;

public class LeagueTest {

    @Test
    void testDraftReturnsTitle(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        Assertions.assertEquals("Default" , league.getName());
    }

    @Test
    void testDraftReturnsPositions(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        Assertions.assertEquals(new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)), league.getTeamPositions());
    }

    @Test
    void testGetTeamNamesReturnsEmpty(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        Assertions.assertTrue(league.getTeamNames().isEmpty());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeam(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test");
        Assertions.assertEquals("Test", league.getTeamNames().getFirst());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeams(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test0");
        league.addTeam("Test1");
        Assertions.assertEquals(new ArrayList<>(List.of("Test0", "Test1")), league.getTeamNames());
    }

    @Test
    void testGetTeamByNameReturnsTeam(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test");
        Assertions.assertEquals("Test", league.getTeamByName("Test").getName());
    }

    @Test
    void testGetTeamByNameReturnsNull(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        Assertions.assertNull(league.getTeamByName("Test"));
    }

    @Test
    void testGetTeamPlayerMapReflectsAddedPlayer(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test");
        Player player = new Player("Chris Burke");
        league.getTeamByName("Test").addPlayer(player, QB);
        Assertions.assertEquals(QB, league.getTeamByName("Test").getPlayerMap().get(player));
    }

    @Test
    void testGetFreePositionsReturnsFreePositions(){
        League league = new League("Default", new ArrayList<>(List.of(QB, FLEX)));
        league.addTeam("Test");
        league.getTeamByName("Test").addPlayer(new Player("Chris Burke"), QB);
        Assertions.assertEquals(FLEX, league.getTeamByName("Test").getFreePositions().getFirst());
    }

    @Test
    void testPlayerIsRemoved(){
        League league = new League("Default", new ArrayList<>(List.of(QB, FLEX)));
        league.addTeam("Test");
        Player player = new Player("Chris Burke");
        league.getTeamByName("Test").addPlayer(player, QB);
        league.getTeamByName("Test").removePlayer(player);
        Assertions.assertTrue(league.getTeamByName("Test").getPlayerMap().isEmpty());
    }

    @Test
    void testGetTeamNameListReflectsAddedPlayer(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test");
        Player player = new Player("Chris Burke");
        league.getTeamByName("Test").addPlayer(player, QB);
        Assertions.assertEquals("Chris Burke", league.getTeamByName("Test").getPlayerNameList().getFirst());
    }

    @Test
    void testGetCalculatedScoreReturnsSetScore(){
        League league = new League("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX)));
        league.addTeam("Test");
        league.getTeamByName("Test").setCalculatedScore(10);
        Assertions.assertEquals(10, league.getTeamByName("Test").getCalculatedScore());
    }
}
