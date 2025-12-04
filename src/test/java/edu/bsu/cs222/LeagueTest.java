package edu.bsu.cs222;

import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static edu.bsu.cs222.model.Position.*;

public class LeagueTest {
    HashMap<String, Double> getDefaultCoefficientMap(){
        HashMap<String, Double> defaultCoefficientMap = new HashMap<>();
        defaultCoefficientMap.put("rushYards", .1);
        defaultCoefficientMap.put("recYards", .1);
        defaultCoefficientMap.put("passYards", .04);
        defaultCoefficientMap.put("rushTds", 7.0);
        defaultCoefficientMap.put("recTds", 7.0);
        defaultCoefficientMap.put("passTds", 4.0);
        defaultCoefficientMap.put("receptions", 1.0);
        defaultCoefficientMap.put("interceptions", -2.0);
        defaultCoefficientMap.put("fumbles", -2.0);
        defaultCoefficientMap.put("xpMade", 2.0);
        defaultCoefficientMap.put("fgMade", 4.0);
        return defaultCoefficientMap;
    }

    ArrayList<Position> getDefaultPositionList(){
        return new ArrayList<>(List.of(QB, QB, RB, TE, K, FLEX));
    }

    @Test
    void testDraftReturnsTitle(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        Assertions.assertEquals("Default" , league.getName());
    }

    @Test
    void testDraftReturnsPositions(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        Assertions.assertEquals(getDefaultPositionList(), league.getTeamPositions());
    }

    @Test
    void testGetTeamNamesReturnsEmpty(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        Assertions.assertTrue(league.getTeamNames().isEmpty());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeam(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        Assertions.assertEquals("Test", league.getTeamNames().getFirst());
    }

    @Test
    void testGetTeamNamesReturnsAddedTeams(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test0");
        league.addTeam("Test1");
        Assertions.assertEquals(new ArrayList<>(List.of("Test0", "Test1")), league.getTeamNames());
    }

    @Test
    void testGetTeamByNameReturnsTeam(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        Assertions.assertEquals("Test", league.getTeamByName("Test").getName());
    }

    @Test
    void testGetTeamByNameReturnsNull(){
        League league = new League("Default",getDefaultPositionList(), getDefaultCoefficientMap());
        Assertions.assertNull(league.getTeamByName("Test"));
    }

    @Test
    void testGetTeamPlayerMapReflectsAddedPlayer(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        Player player = new Player("Chris Burke", null);
        league.getTeamByName("Test").addPlayer(player, QB);
        Assertions.assertEquals(QB, league.getTeamByName("Test").getPlayerMap().get(player));
    }

    @Test
    void testGetFreePositionsReturnsFreePositions(){
        League league = new League("Default", new ArrayList<>(List.of(QB, FLEX)), getDefaultCoefficientMap());
        league.addTeam("Test");
        league.getTeamByName("Test").addPlayer(new Player("Chris Burke", null), QB);
        Assertions.assertEquals(FLEX, league.getTeamByName("Test").getFreePositions().getFirst());
    }

    @Test
    void testPlayerIsRemoved(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        Player player = new Player("Chris Burke", null);
        league.getTeamByName("Test").addPlayer(player, QB);
        league.getTeamByName("Test").removePlayer(player);
        Assertions.assertTrue(league.getTeamByName("Test").getPlayerMap().isEmpty());
    }

    @Test
    void testGetTeamNameListReflectsAddedPlayer(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        Player player = new Player("Chris Burke", null);
        league.getTeamByName("Test").addPlayer(player, QB);
        Assertions.assertEquals("Chris Burke", league.getTeamByName("Test").getPlayerNameList().getFirst());
    }

    @Test
    void testGetCalculatedScoreReturnsSetScore(){
        League league = new League("Default", getDefaultPositionList(), getDefaultCoefficientMap());
        league.addTeam("Test");
        league.getTeamByName("Test").setCalculatedScore(10);
        Assertions.assertEquals(10, league.getTeamByName("Test").getCalculatedScore());
    }

    @Test
    void testGetFileSafeNameReturnsCleanedName(){
        League league = new League("  [New] League\\/:*\"<>|  ", getDefaultPositionList(), getDefaultCoefficientMap());
        Assertions.assertEquals("[New]_League________", league.getFileSafeName());
    }

    @Test
    void testJsonLeagueConstructorReturnsProperCoefficientMap() throws IOException {
        League testLeague = new League(readSampleFileAsString());
        Assertions.assertEquals(getDefaultCoefficientMap(), testLeague.getCoefficientMap());
    }

    @Test
    void textRemovedTeamIsNoLongerInTeamList(){
        League testLeague = new League("testLeague", getDefaultPositionList(), getDefaultCoefficientMap());
        testLeague.addTeam("testTeam");
        testLeague.removeTeam(testLeague.getTeamByName("testTeam"));
        Assertions.assertNull(testLeague.getTeamByName("testTeam"));
    }

    @Test
    void testExtraPlayerRemovedWhenLessPositionsIsTrue(){
        League testLeague = new League("testLeague", getDefaultPositionList(), getDefaultCoefficientMap());
        testLeague.addTeam("testTeam");
        League.Team testTeam = testLeague.getTeamByName("testTeam");
        testTeam.addPlayer(new Player("Chris", "0"), FLEX);
        testLeague.setTeamPositions(new ArrayList<>(List.of(QB, QB, RB, TE, K)), true);
        Assertions.assertTrue(testTeam.getPlayerNameList().isEmpty());
    }

    @Test
    void testSetPositionsUpdatesTeamFreePositions(){
        League testLeague = new League("testLeague", new ArrayList<>(List.of(FLEX)), getDefaultCoefficientMap());
        testLeague.addTeam("testTeam");
        League.Team testTeam = testLeague.getTeamByName("testTeam");
        testLeague.setTeamPositions(getDefaultPositionList(), false);
        Assertions.assertEquals(getDefaultPositionList(), testTeam.getFreePositions());
    }

    @Test
    void testSetPositionsUpdatesTeamFreePositionsAndRemovesPlayerPositions(){
        League testLeague = new League("testLeague", new ArrayList<>(List.of(FLEX)), getDefaultCoefficientMap());
        testLeague.addTeam("testTeam");
        League.Team testTeam = testLeague.getTeamByName("testTeam");
        testTeam.addPlayer(new Player("Chris", "0"), FLEX);
        testLeague.setTeamPositions(getDefaultPositionList(), false);

        ArrayList<Position> newList = new ArrayList<>(getDefaultPositionList());
        newList.remove(FLEX);
        Assertions.assertEquals(newList, testTeam.getFreePositions());
    }

    @Test
    void testSetCoefficientMapUpdatesTeamCoefficientMap(){
        League testLeague = new League("testLeague", getDefaultPositionList(), getDefaultCoefficientMap());
        testLeague.addTeam("testTeam");
        League.Team testTeam = testLeague.getTeamByName("testTeam");

        HashMap<String, Double> newMap = new HashMap<>(getDefaultCoefficientMap());
        newMap.put("interceptions", -1.0);
        testLeague.setCoefficientMap(newMap);

        Assertions.assertEquals(newMap, testTeam.getCoefficientMap());
    }

    @Test
    void testGetSavedReturnsTrueAfterSaving(){
        League testLeague = new League("testLeague", getDefaultPositionList(), getDefaultCoefficientMap());
        testLeague.saveLeague();
        testLeague.deleteLeague();
        Assertions.assertTrue(testLeague.getSaved());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("TestLeague.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
