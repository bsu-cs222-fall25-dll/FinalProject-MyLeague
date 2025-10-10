package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class PlayerRetriever {
    private static final String API_KEY = Dotenv.load().get("API_KEY");

    private String getPlayers() throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com/getNFLPlayerList"))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", "tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch (IOException e){
            return "";
        }
    }

    public void createPlayerList() throws InterruptedException {
        JSONObject jsonObject = new JSONObject(getPlayers());
        JSONArray players = jsonObject.getJSONArray("body");
        for (int i = 0; i < players.length(); ++i){

        }
    }
}
