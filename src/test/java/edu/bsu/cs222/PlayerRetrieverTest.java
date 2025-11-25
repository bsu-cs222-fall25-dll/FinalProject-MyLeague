package edu.bsu.cs222;

import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.PlayerRetriever;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerRetrieverTest {
    @Test
    void testFirstPlayerNameInListIsTysonWilliams() throws IOException {
        PlayerRetriever.createPlayerList(readSampleFileAsString());
        Assertions.assertEquals("Ty'Son Williams", PlayerRetriever.getPlayerArrayList().getFirst().getName());
    }

    @Test
    void testGetPlayersFromApiDoesNotReturnNull() throws InterruptedException, IOException {
        Assertions.assertNotNull(PlayerRetriever.getPlayersFromApi());
    }

    @Test
    void testGetPlayersFromJsonDoesNotReturnNullAfterSaving() throws IOException, InterruptedException {
        PlayerRetriever.createAndSavePlayerListFromApi();
        Assertions.assertNotNull(PlayerRetriever.getPlayersFromJson());
    }

    @Test
    void testGetPlayersFromJsonOrAPISavesJsonAfterDeletingJson() throws IOException, InterruptedException {

        File file = new File("src/main/resources/PlayerList.json");
        if (file.exists()) {
            if (!file.delete()){
                Assertions.fail();
            }
        }
        PlayerRetriever.getPlayersFromJsonOrApi();
        Assertions.assertNotNull(PlayerRetriever.getPlayersFromJson());
    }

    @Test
    void testGetPlayersFromJsonEqualsPlayerListAfterSaving() throws IOException, InterruptedException {
        PlayerRetriever.createAndSavePlayerListFromApi();
        ArrayList<Player> originalPlayerList = PlayerRetriever.getPlayerArrayList();
        PlayerRetriever.createPlayerList(PlayerRetriever.getPlayersFromJson());
        Assertions.assertEquals(originalPlayerList.getFirst().getName(), PlayerRetriever.getPlayerArrayList().getFirst().getName());
    }

    @Test
    void testCreateAndSavePlayerListFromApiCreatesPlayerList() throws InterruptedException, IOException {
        PlayerRetriever.createAndSavePlayerListFromApi();
        Assertions.assertNotNull(PlayerRetriever.getPlayerArrayList());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
