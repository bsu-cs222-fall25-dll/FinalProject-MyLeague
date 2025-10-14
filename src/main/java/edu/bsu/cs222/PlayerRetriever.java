package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerRetriever {
    private ArrayList<Player> playerArrayList = new ArrayList<>();
    private static final String API_KEY = Dotenv.load().get("API_KEY");

    public void createAndSavePlayerListFromApi() throws InterruptedException, IOException {
        createPlayerList(getPlayersFromApi());
        savePlayerListToJson();
    }

    public String getPlayersFromApi() throws InterruptedException {
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

    public String getPlayersFromJson() throws IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("src/main/resources/PlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }

    public void createPlayerList(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray players = jsonObject.getJSONArray("body");
        for (int i = 0; i < players.length(); ++i){
            JSONObject player = players.getJSONObject(i);
            String espnName = player.getString("espnName");
            String pos = player.getString("pos");
            String team = player.getString("team");
            String jerseyNum = player.getString("jerseyNum");
            String height = player.getString("height");
            String weight = player.getString("weight");
            String age = "";
            try {
                age = player.getString("age");
            } catch (JSONException e){
                age = "not found";
            }
            String bDay = "";
            try {
                bDay = player.getString("bDay");
            } catch (JSONException e){
                bDay = "not found";
            }
            String espnHeadshot = "";
            try {
                espnHeadshot = player.getString("espnHeadshot");
            } catch (JSONException e){
                bDay = "not found";
            }
            JSONObject injury = player.getJSONObject("injury");
            String school = player.getString("school");
            String playerID = player.getString("playerID");
            String teamID = player.getString("teamID");
            String exp = player.getString("exp");
            Player newPlayer = new Player(espnName, pos, team, jerseyNum, height, weight, age, bDay,
                    espnHeadshot, injury, school, playerID, teamID, exp);
            playerArrayList.add(newPlayer);
        }
    }

    public ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

    private void savePlayerListToJson() throws IOException {
        JSONArray players = new JSONArray();

        for (Player player: getPlayerArrayList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("espnName", player.getName());
            jsonObject.put("pos", player.getPosition());
            jsonObject.put("team", player.getTeam());
            jsonObject.put("jerseyNum", player.getJerseyNumber());
            jsonObject.put(("height"), player.getHeight());
            jsonObject.put("weight", player.getWeight());
            jsonObject.put("age", player.getAge());
            jsonObject.put("bDay", player.getbDay());
            jsonObject.put("espnHeadshot", player.getHeadshot());
            jsonObject.put("injury", player.getInjury());
            jsonObject.put("school", player.getSchool());
            jsonObject.put("playerID", player.getPlayerID());
            jsonObject.put("teamID", player.getTeamID());
            jsonObject.put("exp", player.getExperience());
            players.put(jsonObject);
        }

        String jsonData = players.toString();

        try(FileWriter file = new FileWriter("src/main/resources/PlayerList.json")){
            file.write(jsonData);
        }
    }
}
