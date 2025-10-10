package edu.bsu.cs222;

import com.jayway.jsonpath.JsonPath;
import io.github.cdimascio.dotenv.Dotenv;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public class LearningTest {

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
        Assertions.assertEquals(11, getPlayersFromJson(jsonData).size());
    }

    private String readSampleFileAsString() throws NullPointerException, IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testPlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }

    private int getStatusCodeFromJson(String jsonData) {
        JSONArray jsonArray = JsonPath.read(jsonData, "$..statusCode");
        return (int) jsonArray.getFirst();
    }

    private JSONArray getPlayersFromJson(String jsonData){
        return JsonPath.read(jsonData, "$..body[*]");
    }
}
