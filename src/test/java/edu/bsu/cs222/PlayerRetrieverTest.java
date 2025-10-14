package edu.bsu.cs222;

import org.json.JSONArray;
import org.json.JSONObject;
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

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }
}
