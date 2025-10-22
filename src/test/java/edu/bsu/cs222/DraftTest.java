package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.bsu.cs222.gui.controllers.Position.*;

public class DraftTest {

    @Test
    void testDraftReturnsTitle(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        Assertions.assertEquals("Default" , draft.getTitle());
    }

    @Test
    void testDraftReturnsPositions(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        Assertions.assertEquals(new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)), draft.getTeamPositions());
    }

    @Test
    void testGetTeamNamesReturnsEmpty(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        Assertions.assertTrue(draft.getTeamNames().isEmpty());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeam(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        draft.addTeam("Test");
        Assertions.assertEquals("Test", draft.getTeamNames().getFirst());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeams(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        draft.addTeam("Test0");
        draft.addTeam("Test1");
        Assertions.assertEquals(new ArrayList<>(List.of("Test0", "Test1")), draft.getTeamNames());
    }

    @Test
    void testGetTeamByNameReturnsTeam(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        draft.addTeam("Test");
        Assertions.assertEquals("Test", draft.getTeamByName("Test").getName());
    }

    @Test
    void testGetTeamByNameReturnsNull(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        Assertions.assertNull(draft.getTeamByName("Test"));
    }

    @Test
    void testGetTeamByNameReflectsAddedPlayer(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, QB, RB, TE, K, Flex)));
        draft.addTeam("Test");
        Player player = new Player("Chris Burke");
        draft.getTeamByName("Test").addPlayer(player, QB);
        Assertions.assertEquals(player, draft.getTeamByName("Test").getPlayers().getFirst());
    }

    @Test
    void testGetFreePositionsReturnsFreePositions(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, Flex)));
        draft.addTeam("Test");
        draft.getTeamByName("Test").addPlayer(new Player("Chris Burke"), QB);
        Assertions.assertEquals(Flex, draft.getTeamByName("Test").getFreePositions().getFirst());
    }

    @Test
    void testPlayerIsRemoved(){
        Draft draft = new Draft("Default", new ArrayList<>(List.of(QB, Flex)));
        draft.addTeam("Test");
        Player player = new Player("Chris Burke");
        draft.getTeamByName("Test").addPlayer(player, QB);
        draft.getTeamByName("Test").removePlayer(player);
        Assertions.assertTrue(draft.getTeamByName("Test").getPlayers().isEmpty());
    }
}
