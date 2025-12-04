package edu.bsu.cs222;

import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;

import static edu.bsu.cs222.model.Position.*;

public class PlayerTest {
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

    @Test
    void getWeekScoreTest() {
        Player burrow = new Player("Burrow", null);
        burrow.setLastStatDate(LocalDate.now())
;
        HashMap<String, Integer> playerStats = new HashMap<>();
        playerStats.put("weekRushYds", 30);
        playerStats.put("weekRushTD", 1);
        playerStats.put("weekRecTD", 0);
        playerStats.put("weekRecYds", 30);
        playerStats.put("weekPassTD", 3);
        playerStats.put("weekPassYds", 250);
        playerStats.put("weekReceptions", 2);
        playerStats.put("weekInterceptions", 1);
        playerStats.put("weekFumbles", 1);
        playerStats.put("weekFgMade", 0);
        playerStats.put("weekFgAttempts", 0);
        playerStats.put("weekXpMade", 0);
        playerStats.put("weekXpAttempts", 0);

        burrow.setPlayerStats(playerStats);
        Assertions.assertEquals(33, burrow.getWeekScore(getDefaultCoefficientMap()));
    }

    @Test
    void testKickerPoints() {
        Player younghoeKoo = new Player("Younghoe Koo", null);
        younghoeKoo.setLastStatDate(LocalDate.now());

        HashMap<String, Integer> playerStats = new HashMap<>();
        playerStats.put("weekXpAttempts", 3);
        playerStats.put("weekXpMade", 2);
        playerStats.put("weekFgAttempts", 4);
        playerStats.put("weekFgMade", 3);
        playerStats.put("weekRecYds", 0);
        playerStats.put("weekRecTD", 0);
        playerStats.put("weekReceptions", 0);
        playerStats.put("weekRushYds", 0);
        playerStats.put("weekRushTD", 0);
        playerStats.put("weekPassYds", 0);
        playerStats.put("weekPassTD", 0);
        playerStats.put("weekInterceptions", 0);
        playerStats.put("weekFumbles", 0);

        younghoeKoo.setPlayerStats(playerStats);
        Assertions.assertEquals(9, younghoeKoo.getWeekScore(getDefaultCoefficientMap()));
    }

    @Test
    void testCustomPlayerEqualsWithDifferentMemoryAddress(){
        Player player0 = new Player("Chris", "12345");
        Player player1 = new Player("Chris", "12345");
        Assertions.assertEquals(player0, player1);
    }

    @Test
    void testCustomPlayerEqualsWithSameMemoryAddress(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertEquals(player0, player0);
    }

    @Test
    void testCustomerPlayerEqualsWithNull(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertNotEquals(null, player0);
    }

    @Test
    void testCustomerPlayerEqualsWithDifferentObject(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertNotEquals(new Object(), player0);
    }

    @Test
    void testCustomPlayerMapContainsPlayer(){
        Player player0 = new Player("Chris", "12345");
        Player player1 = new Player("Chris", "12345");
        HashMap<Player, Position> map = new HashMap<>();
        map.put(player0, QB);
        Assertions.assertTrue(map.containsKey(player1));
    }

    private String readSampleFileAsString(String fileName) throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName + ".json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }

    @Test
    void testSetPlayerStatsBrandonAubreyKicking() throws IOException {
        Player brandon = new Player("Brandon", null);
        brandon.setPlayerStats(readSampleFileAsString("BrandonAubrey"));
        Assertions.assertEquals(17, brandon.getPlayerStats().get("seasonFgMade"));
    }
    @Test
    void testTaysomHillSeasonScore() throws IOException {
        Player taysom = new Player("Taysom", null);
        taysom.setPlayerStats(readSampleFileAsString("TaysomHill"));
        Assertions.assertEquals(17.76, Math.round(taysom.getSeasonScore(getDefaultCoefficientMap()) *100.0) / 100.0);
    }
}
