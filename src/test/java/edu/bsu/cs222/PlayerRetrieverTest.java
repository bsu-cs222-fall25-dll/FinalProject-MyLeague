package edu.bsu.cs222;

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
        PlayerRetriever retriever = new PlayerRetriever();
        retriever.createPlayerList(readSampleFileAsString());
        Assertions.assertEquals("Ty'Son Williams", retriever.getPlayerArrayList().getFirst().getName());
    }

    @Test
    void testGetPlayersFromApiDoesNotReturnNull() throws InterruptedException {
        PlayerRetriever retriever = new PlayerRetriever();
        Assertions.assertNotNull(retriever.getPlayersFromApi());
    }

    @Test
    void testGetPlayersFromJsonDoesNotReturnNullAfterSaving() throws IOException, InterruptedException {
        PlayerRetriever retriever = new PlayerRetriever();
        retriever.createAndSavePlayerListFromApi();
        Assertions.assertNotNull(retriever.getPlayersFromJson());
    }

    @Test
    void testGetPlayersFromJsonOrAPIDoesNotReturnNullAfterDeletingJson() throws IOException, InterruptedException {
        if (new File("src/main/resources/PlayerList.json").delete()){
            PlayerRetriever retriever = new PlayerRetriever();
            retriever.getPlayersFromJsonOrApi();
            Assertions.assertNotNull(retriever.getPlayersFromJson());
        }
    }

    @Test
    void testGetPlayersFromJsonEqualsPlayerListAfterSaving() throws IOException, InterruptedException {
        PlayerRetriever retriever = new PlayerRetriever();
        retriever.createAndSavePlayerListFromApi();
        ArrayList<Player> originalPlayerList = retriever.getPlayerArrayList();
        retriever.createPlayerList(retriever.getPlayersFromJson());
        Assertions.assertEquals(originalPlayerList.getFirst().getName(), retriever.getPlayerArrayList().getFirst().getName());
    }

    @Test
    void testCreateAndSavePlayerListFromApiCreatesPlayerList() throws InterruptedException, IOException {
        PlayerRetriever retriever = new PlayerRetriever();
        retriever.createAndSavePlayerListFromApi();
        Assertions.assertNotNull(retriever.getPlayerArrayList());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
