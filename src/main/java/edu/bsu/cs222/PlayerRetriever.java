package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;

public class PlayerRetriever {
    private ArrayList<Player> playerArrayList = new ArrayList<>();
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

    public void createPlayerList(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray players = jsonObject.getJSONArray("body");
        for (int i = 0; i < players.length(); ++i){
            JSONObject player = players.getJSONObject(i);
            String espnName = player.getString("espnName");
            String pos = player.getString("pos");
            String team = player.getString("team");
            int jerseyNum = player.getInt("jerseyNum");
            int height = player.getInt("height");
            int weight = player.getInt("weight");
            int age = player.getInt("age");
            String bDay = player.getString("bDay");
            String espnHeadshot = player.getString("espnHeadshot");
            JSONObject injury = player.getJSONObject("injury");
            String school = player.getString("school");
            String playerID = player.getString("playerID");
            String teamID = player.getString("teamID");
            int exp = player.getInt("exp");
            Player newPlayer = new Player(espnName, pos, team, jerseyNum, height, weight, age, bDay,
                    espnHeadshot, injury, school, playerID, teamID, exp);
            playerArrayList.add(newPlayer);
        }
    }

    public ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }
}
