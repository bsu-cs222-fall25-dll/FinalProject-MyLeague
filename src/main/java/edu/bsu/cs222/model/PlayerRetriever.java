package edu.bsu.cs222.model;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
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
        InputStream jsonFile = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("PlayerList.json");
        return new String(Objects.requireNonNull(jsonFile).readAllBytes(), Charset.defaultCharset());
    }

    public static void createPlayerList(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray players = jsonObject.getJSONArray("body");
        playerArrayList = new ArrayList<>();
        for (int i = 0; i < players.length(); ++i){
            JSONObject player = players.getJSONObject(i);
            HashMap<String, String> playerInfo = new HashMap<>();
            String posString = player.getString("pos");

            String name = player.getString("espnName");

            if (posString.equals("FB")){
                posString = "RB";
            }

            if (posString.equals("PK")){
                posString = "K";
            }

            if (name.equals("Taysom Hill")){
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
                playerInfo.put("name", name);
                playerInfo.put("position", posString);
                playerInfo.put("team", player.getString("team"));
                playerInfo.put("jerseyNumber", player.getString("jerseyNum"));
                playerInfo.put("height", player.getString("height"));
                playerInfo.put("weight", player.getString("weight"));
                playerInfo.put("school", player.getString("school"));
                playerInfo.put("playerID", player.getString("playerID"));
                playerInfo.put("teamID", player.getString("teamID"));
                String exp = player.getString("exp");
                if (exp.equals("R")){
                    exp = "0";
                }
                playerInfo.put("experience", exp);

                String age;

                try {
                    age = player.getString("age");
                } catch (JSONException e) {
                    age = "not found";
                }
                playerInfo.put("age", age);

                String bDay;
                try {
                    bDay = player.getString("bDay");
                } catch (JSONException e) {
                    bDay = "not found";
                }
                playerInfo.put("bDay", bDay);

                String headshot;
                try {
                    headshot = player.getString("espnHeadshot");
                } catch (JSONException e) {
                    headshot = "not found";
                }
                playerInfo.put("headshot", headshot);

                Player newPlayer = new Player(playerInfo);

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
            jsonObject.put("school", player.getSchool());
            jsonObject.put("playerID", player.getPlayerID());
            jsonObject.put("teamID", player.getTeamID());
            jsonObject.put("exp", player.getExperience());
            playersJsonArray.put(jsonObject);
        }

        JSONObject playersJsonObject = new JSONObject();
        playersJsonObject.put("body", playersJsonArray);

        String jsonData = playersJsonObject.toString();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/PlayerList.json"))){
            writer.write(jsonData);
        }
    }
}
