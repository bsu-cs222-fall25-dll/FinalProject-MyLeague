package edu.bsu.cs222.model;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.HashMap;
import java.util.Objects;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class Player {
    private static String API_KEY;

    static {
        try {
            API_KEY = Dotenv.load().get("API_KEY");
        } catch (DotenvException _) {
            System.out.println("Add your API key to .env.example, and rename file to .env");
        }
    }
    private Position position;
    private final String playerID;
    private final HashMap <String, String> nonScoringStats = new HashMap<>();
    //Above are stats shown from player list, below are stats which require a deeper API call.
    private HashMap <String, Integer> playerStats = new HashMap<>();
    private String lastGame;
    private LocalDate lastStatDate;


    public Player(HashMap<String, String> playerInfo) {
        nonScoringStats.put("name", playerInfo.get("playerName"));
        this.position = Position.valueOf(playerInfo.get("position"));
        nonScoringStats.put("team", playerInfo.get("team"));
        nonScoringStats.put("jerseyNumber", playerInfo.get("jerseyNumber"));
        nonScoringStats.put("height", playerInfo.get("height"));
        nonScoringStats.put("weight", playerInfo.get("weight"));
        nonScoringStats.put("age", playerInfo.get("age"));
        nonScoringStats.put("headshot", playerInfo.get("headshot"));
        nonScoringStats.put("school", playerInfo.get("school"));
        this.playerID = playerInfo.get("playerID");
        nonScoringStats.put("experience", playerInfo.get("experience"));
    }

    //Scoring methods
    public double getWeekScore(HashMap<String, Double> coefficientMap) {
        return (playerStats.get("weekRushYds") * coefficientMap.get("rushYards") +
                playerStats.get("weekRecYds") * coefficientMap.get("recYards") +
                playerStats.get("weekRushTD") * coefficientMap.get("rushTds") +
                playerStats.get("weekRecTD") * coefficientMap.get("recTds") +
                playerStats.get("weekPassTD") * coefficientMap.get("passTds") +
                playerStats.get("weekPassYds") * coefficientMap.get("passYards") +
                playerStats.get("weekReceptions") * coefficientMap.get("receptions") +
                playerStats.get("weekInterceptions") * coefficientMap.get("interceptions") +
                playerStats.get("weekFumbles") * coefficientMap.get("fumbles") -
                playerStats.get("weekXpAttempts") + playerStats.get("weekXpMade") * coefficientMap.get("xpMade") -
                playerStats.get("weekFgAttempts") + playerStats.get("weekFgMade") * coefficientMap.get("fgMade"));
    }

    public double getSeasonScore(HashMap<String, Double> coefficientMap) {
        return (playerStats.get("seasonRushYds") * coefficientMap.get("rushYards") +
                playerStats.get("seasonRecYds") * coefficientMap.get("recYards") +
                playerStats.get("seasonRushTD") * coefficientMap.get("rushTds") +
                playerStats.get("seasonRecTD") * coefficientMap.get("recTds") +
                playerStats.get("seasonPassTD") * coefficientMap.get("passTds") +
                playerStats.get("seasonPassYds") * coefficientMap.get("passYards") +
                playerStats.get("seasonReceptions") * coefficientMap.get("receptions") +
                playerStats.get("seasonInterceptions") * coefficientMap.get("interceptions") +
                playerStats.get("seasonFumbles") * coefficientMap.get("fumbles") -
                playerStats.get("seasonXpAttempts") + playerStats.get("seasonXpMade") * coefficientMap.get("xpMade") -
                playerStats.get("seasonFgAttempts") + playerStats.get("seasonFgMade") * coefficientMap.get("fgMade"));
    }


    public void setStatsWithAPI() throws Exception {
        if (!lastScoreDateIsToday()){
            String response = getStatsFromAPI();
            if (response != null && !response.isBlank()){
                setPlayerStats(response);
            }
        }
    }


    public void setPlayerStats(String jsonData){
        JSONArray playerGames = new JSONObject(jsonData).getJSONArray("body");
        JSONObject currentStats;

        LocalDate today = LocalDate.now();
        lastStatDate = today;
        int seasonYear = today.getMonthValue() >= Month.SEPTEMBER.getValue() ? today.getYear() : today.getYear() - 1;
        LocalDate seasonStart = LocalDate.of(seasonYear, Month.SEPTEMBER, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        LocalDate weekStart = today.getDayOfWeek() == DayOfWeek.TUESDAY ? today : today.with(TemporalAdjusters.previous(DayOfWeek.TUESDAY));

        playerStats.put("weekRecYds", 0);
        playerStats.put("weekRecTD", 0);
        playerStats.put("weekReceptions", 0);
        playerStats.put("weekRushYds", 0);
        playerStats.put("weekRushTD", 0);
        playerStats.put("weekPassYds", 0);
        playerStats.put("weekPassTD", 0);
        playerStats.put("weekInterceptions", 0);
        playerStats.put("weekFgMade", 0);
        playerStats.put("weekFgAttempts", 0);
        playerStats.put("weekXpMade", 0);
        playerStats.put("weekXpAttempts", 0);
        playerStats.put("weekFumbles", 0);

        int seasonRecYds = 0;
        int seasonRecTD = 0;
        int seasonReceptions = 0;
        int seasonRushYds = 0;
        int seasonRushTD = 0;
        int seasonPassYds = 0;
        int seasonPassTD = 0;
        int seasonInterceptions = 0;
        int seasonFgMade = 0;
        int seasonFgAttempts = 0;
        int seasonXpMade = 0;
        int seasonXpAttempts = 0;
        int seasonFumbles = 0;

        JSONObject game = playerGames.getJSONObject(0);
        String gameID = game.getString("gameID");
        lastGame = gameID.substring(9).replace("@", " vs ");


        for (Object gameObject : playerGames){
            game = (JSONObject) gameObject;
            LocalDate gameDate = LocalDate.parse(game.getString("gameID").substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));

            if(gameDate.isAfter(weekStart.with(TemporalAdjusters.previous(DayOfWeek.TUESDAY))) && gameDate.isBefore(weekStart)){
                try {
                    currentStats = game.getJSONObject("Receiving"); //Receiving stats
                    playerStats.put("weekRecYds", Integer.parseInt(currentStats.getString("recYds")));
                    playerStats.put("weekRecTD", Integer.parseInt(currentStats.getString("recTD")));
                    playerStats.put("weekReceptions", Integer.parseInt(currentStats.getString("receptions")));
                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Rushing"); //Rushing stats
                    playerStats.put("weekRushYds", Integer.parseInt(currentStats.getString("rushYds")));
                    playerStats.put("weekRushTD", Integer.parseInt(currentStats.getString("rushTD")));
                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Passing"); //Passing stats
                    playerStats.put("weekPassYds", Integer.parseInt(currentStats.getString("passYds")));
                    playerStats.put("weekPassTD", Integer.parseInt(currentStats.getString("passTD")));
                    playerStats.put("weekInterceptions", Integer.parseInt(currentStats.getString("int")));
                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Kicking"); //Passing stats
                    playerStats.put("weekFgMade", Integer.parseInt(currentStats.getString("fgMade")));
                    playerStats.put("weekFgAttempts", Integer.parseInt(currentStats.getString("fgAttempts")));
                    playerStats.put("weekXpMade", Integer.parseInt(currentStats.getString("xpMade")));
                    playerStats.put("weekXpAttempts", Integer.parseInt(currentStats.getString("xpAttempts")));
                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Defense"); //For fumbles
                    playerStats.put("weekFumbles", Integer.parseInt(currentStats.getString("fumblesLost")));
                } catch (JSONException ignored) {}
            }

            if (gameDate.isAfter(seasonStart)){
                try {
                    currentStats = game.getJSONObject("Receiving"); //Receiving stats
                    seasonRecYds += Integer.parseInt(currentStats.getString("recYds"));
                    seasonRecTD += Integer.parseInt(currentStats.getString("recTD"));
                    seasonReceptions += Integer.parseInt(currentStats.getString("receptions"));
                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Rushing"); //Rushing stats
                    seasonRushYds += Integer.parseInt(currentStats.getString("rushYds"));
                    seasonRushTD += Integer.parseInt(currentStats.getString("rushTD"));
                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Passing"); //Passing stats
                    seasonPassYds += Integer.parseInt(currentStats.getString("passYds"));
                    seasonPassTD += Integer.parseInt(currentStats.getString("passTD"));
                    seasonInterceptions += Integer.parseInt(currentStats.getString("int"));
                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Kicking"); //Kicking stats
                    seasonFgMade += Integer.parseInt(currentStats.getString("fgMade"));
                    seasonFgAttempts += Integer.parseInt(currentStats.getString("fgAttempts"));
                    seasonXpMade += Integer.parseInt(currentStats.getString("xpMade"));
                    seasonXpAttempts += Integer.parseInt(currentStats.getString("xpAttempts"));
                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Defense"); //For fumbles
                    seasonFumbles += Integer.parseInt(currentStats.getString("fumblesLost"));
                } catch (JSONException ignored) {}
            }
            else {
                break;
            }
        }

        playerStats.put("seasonRecYds", seasonRecYds);
        playerStats.put("seasonRecTD", seasonRecTD);
        playerStats.put("seasonReceptions", seasonReceptions);
        playerStats.put("seasonRushYds", seasonRushYds);
        playerStats.put("seasonRushTD", seasonRushTD);
        playerStats.put("seasonPassYds", seasonPassYds);
        playerStats.put("seasonPassTD", seasonPassTD);
        playerStats.put("seasonInterceptions", seasonInterceptions);
        playerStats.put("seasonFgMade", seasonFgMade);
        playerStats.put("seasonFgAttempts", seasonFgAttempts);
        playerStats.put("seasonXpMade", seasonXpMade);
        playerStats.put("seasonXpAttempts", seasonXpAttempts);
        playerStats.put("seasonFumbles", seasonFumbles);
    }

    private String getStatsFromAPI() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com/getNFLGamesForPlayer?playerID="+playerID+"&itemFormat=list&numberOfGames=20"))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", "tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private boolean lastScoreDateIsToday(){
        return lastStatDate != null && lastStatDate.equals(LocalDate.now());
    }

    // Getters
    public HashMap<String, Integer> getPlayerStats() {
        return playerStats;
    }

    public HashMap<String, String> getNonScoringStats(){
        return nonScoringStats;
    }

    public String getLastGame() {
        return lastGame;
    }

    public Position getPosition() {
        return position;
    }

    public String getPlayerID() {
        return playerID;
    }
    //Constructors and Setters for tests
    public Player (String name, String playerID) {
        nonScoringStats.put("name", name);
        this.playerID = playerID;
    }

    public void setPlayerStats(HashMap<String, Integer> playerStats){
        this.playerStats = playerStats;
    }

    public void setLastStatDate(LocalDate lastStatDate) {
        this.lastStatDate = lastStatDate;
    }


    //Functions to set player equality by id
    @Override
    public boolean equals(Object object){
        if (this == object) {return true;}
        if (object == null || getClass() != object.getClass()) {return false;}

        Player player = (Player) object;
        return Objects.equals(this.playerID, player.playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerID);
    }
}
