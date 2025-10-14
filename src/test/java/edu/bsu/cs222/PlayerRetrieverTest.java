package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class PlayerRetrieverTest {

    @Test
    void firstPlayerNameInListIsJackCoco() throws IOException {
        PlayerRetriever retriever = new PlayerRetriever();
        retriever.createPlayerList(readSampleFileAsString());
        Assertions.assertEquals("Jack Coco", retriever.getPlayerArrayList().getFirst().getName());
    }

    @Test
    void getPlayersDoesNotReturnNull() throws InterruptedException {
        PlayerRetriever retriever = new PlayerRetriever();
        Assertions.assertNotNull(retriever.getPlayers());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
