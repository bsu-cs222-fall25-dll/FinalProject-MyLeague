package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class LearningTest {
    // Fails without API_KEY in .env
    @Test
    void testAccessToAPIKey(){
        final String API_KEY = Dotenv.load().get("API_KEY");
        Assertions.assertNotNull(API_KEY);
    }

    @Test
    void testAccessToJsonFile() throws IOException {
        String jsonData = readSampleFileAsString();
        Assertions.assertNotNull(jsonData);
    }

    @Test
    void testStatusCode() throws IOException {
        String jsonData = readSampleFileAsString();
        Assertions.assertEquals(200, getStatusCodeFromJson(jsonData));
    }

    @Test
    void testNumberOfPlayers() throws IOException {
        String jsonData = readSampleFileAsString();
        Assertions.assertEquals(11, getPlayersFromJson(jsonData).length());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }

    private int getStatusCodeFromJson(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getInt("statusCode");
    }

    private JSONArray getPlayersFromJson(String jsonData){
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getJSONArray("body");
    }
}
