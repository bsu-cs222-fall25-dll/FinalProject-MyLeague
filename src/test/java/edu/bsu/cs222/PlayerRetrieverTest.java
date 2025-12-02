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

    // Fails without API_KEY in .env or without a network connection
    @Test
    void testGetPlayersFromApiDoesNotReturnNull() throws InterruptedException {
        if (!PlayerRetriever.getKeyLoaded()){
            Assertions.fail("Requires API_KEY in .env");
            return;
        }

        String players;
        try {
            players = PlayerRetriever.getPlayersFromApi();
        } catch (IOException _){
            Assertions.fail("Requires network connection");
            return;
        }

        Assertions.assertNotNull(players);
    }

    // Fails without API_KEY in .env or without a network connection
    @Test
    void testGetPlayersFromJsonDoesNotReturnNullAfterSaving() throws InterruptedException, IOException {
        if (!PlayerRetriever.getKeyLoaded()){
            Assertions.fail("Requires API_KEY in .env");
            return;
        }

        try {
            PlayerRetriever.createAndSavePlayerListFromApi();
        } catch (IOException _) {
            Assertions.fail("Requires network connection");
            return;
        }

        Assertions.assertNotNull(PlayerRetriever.getPlayersFromJson());
    }

    // Fails without API_KEY in .env or without a network connection
    @Test
    void testGetPlayersFromJsonOrAPISavesJsonAfterDeletingJson() throws InterruptedException, IOException {
        if (!PlayerRetriever.getKeyLoaded()){
            Assertions.fail("Requires API_KEY in .env");
            return;
        }

        File file = new File("src/main/resources/PlayerList.json");
        if (file.exists()) {
            if (!file.delete()){
                Assertions.fail("File failed to delete");
            }
        }

        try {
            PlayerRetriever.getPlayersFromJsonOrApi();
        } catch (IOException _) {
            Assertions.fail("Requires network connection");
            return;
        }

        Assertions.assertNotNull(PlayerRetriever.getPlayersFromJson());
    }

    // Fails without API_KEY in .env or without a network connection
    @Test
    void testGetPlayersFromJsonEqualsPlayerListAfterSaving() throws InterruptedException, IOException {
        if (!PlayerRetriever.getKeyLoaded()){
            Assertions.fail("Requires API_KEY in .env");
            return;
        }

        try {
            PlayerRetriever.createAndSavePlayerListFromApi();
        } catch (IOException _) {
            Assertions.fail("Requires network connection");
            return;
        }

        ArrayList<Player> originalPlayerList = PlayerRetriever.getPlayerArrayList();
        PlayerRetriever.createPlayerList(PlayerRetriever.getPlayersFromJson());
        Assertions.assertEquals(originalPlayerList.getFirst().getName(), PlayerRetriever.getPlayerArrayList().getFirst().getName());
    }

    // Fails without API_KEY in .env or without a network connection
    @Test
    void testCreateAndSavePlayerListFromApiCreatesPlayerList() throws InterruptedException {
        if (!PlayerRetriever.getKeyLoaded()){
            Assertions.fail("Requires API_KEY in .env");
            return;
        }

        try {
            PlayerRetriever.createAndSavePlayerListFromApi();
        } catch (IOException _) {
            Assertions.fail("Requires network connection");
            return;
        }

        Assertions.assertNotNull(PlayerRetriever.getPlayerArrayList());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
