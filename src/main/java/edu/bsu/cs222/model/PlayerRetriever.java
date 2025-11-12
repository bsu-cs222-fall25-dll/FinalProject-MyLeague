package edu.bsu.cs222.model;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerRetriever {
    private static ArrayList<Player> playerArrayList;
    private static final String API_KEY = Dotenv.load().get("API_KEY");

    public static boolean createAndSavePlayerListFromApi() throws InterruptedException, IOException {
        String response = getPlayersFromApi();
        if (response.equals("Network Error")){
            //Can't be tested
            return true;
        }
        createPlayerList(response);
        savePlayerListToJson();
        return false;
    }

    public static boolean getPlayersFromJsonOrApi() throws IOException, InterruptedException {
        File jsonFile = new File("src/main/resources/PlayerList.json");
        if (jsonFile.exists()){
            createPlayerList(getPlayersFromJson());
            return false;
        }
        else {
            return createAndSavePlayerListFromApi();
        }
    }

    public static String getPlayersFromApi() throws InterruptedException {
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
            //Can't be tested
            return "Network Error";
        }
    }

    public static String getPlayersFromJson() throws IOException {
        InputStream sampleFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("PlayerList.json");
        return new String(Objects.requireNonNull(sampleFile).readAllBytes(), Charset.defaultCharset());
    }

    public static void createPlayerList(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray players = jsonObject.getJSONArray("body");
        playerArrayList = new ArrayList<>();
        for (int i = 0; i < players.length(); ++i){
            JSONObject player = players.getJSONObject(i);
            String posString = player.getString("pos");
            String espnName = player.getString("espnName");

            if (posString.equals("FB")){
                posString = "RB";
            }

            if (posString.equals("PK")){
                posString = "K";
            }

            if (espnName.equals("Taysom Hill")){
                posString = "TE";
            }

            Position posObject = null;

            for (Position position : Position.values()){
                if (posString.equals(position.toString())){
                    posObject = position;
                    break;
                }
            }

            if (posObject != null) {
                String team = player.getString("team");
                String jerseyNum = player.getString("jerseyNum");
                String height = player.getString("height");
                String weight = player.getString("weight");
                String school = player.getString("school");
                String playerID = player.getString("playerID");
                String teamID = player.getString("teamID");
                String exp = player.getString("exp");
                if (exp.equals("R")){
                    exp = "0";
                }
                JSONObject injury = player.getJSONObject("injury");

                String age;

                try {
                    age = player.getString("age");
                } catch (JSONException e) {
                    age = "not found";
                }

                String bDay;
                try {
                    bDay = player.getString("bDay");
                } catch (JSONException e) {
                    bDay = "not found";
                }

                String espnHeadshot;
                try {
                    espnHeadshot = player.getString("espnHeadshot");
                } catch (JSONException e) {
                    espnHeadshot = "not found";
                }

                Player newPlayer = new Player(espnName, posObject, team, jerseyNum, height, weight, age, bDay,
                        espnHeadshot, injury, school, playerID, teamID, exp);

                playerArrayList.add(newPlayer);
            }
        }
    }

    public static ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

    private static void savePlayerListToJson() throws IOException {
        JSONArray playersJsonArray = new JSONArray();

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
            playersJsonArray.put(jsonObject);
        }

        JSONObject playersJsonObject = new JSONObject();
        playersJsonObject.put("body", playersJsonArray);

        String jsonData = playersJsonObject.toString();

        try(FileWriter file = new FileWriter("src/main/resources/PlayerList.json")){
            file.write(jsonData);
        }
    }
}
