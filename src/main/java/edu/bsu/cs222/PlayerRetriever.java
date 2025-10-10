package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class PlayerRetriever {
    private static final String API_KEY = Dotenv.load().get("API_KEY");

    public void getPlayers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com/getNFLPlayerList"))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", "tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        PlayerRetriever playerRetriever = new PlayerRetriever();
        playerRetriever.getPlayers();
    }
}
