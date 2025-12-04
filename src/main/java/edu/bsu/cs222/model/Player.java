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
    private static final String[] statsLabels = {"RecYds", "RecTD", "Receptions", "RushYds", "RushTD", "PassYds", "PassTD",
            "Interceptions", "FgMade", "FgAttempts", "XpMade", "XpAttempts", "Fumbles"};

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

        for (String stat: statsLabels){
            playerStats.put(String.format("week%s", stat), 0);
            playerStats.put(String.format("season%s", stat), 0);
        }

        JSONObject game = playerGames.getJSONObject(0);
        String gameID = game.getString("gameID");
        lastGame = gameID.substring(9).replace("@", " vs ");

        for (Object gameObject : playerGames){
            game = (JSONObject) gameObject;
            LocalDate gameDate = LocalDate.parse(game.getString("gameID").substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
            boolean thisWeekGame = gameDate.isAfter(weekStart.with(TemporalAdjusters.previous(DayOfWeek.TUESDAY))) && gameDate.isBefore(weekStart);

            if (gameDate.isAfter(seasonStart)){
                try {
                    currentStats = game.getJSONObject("Receiving"); //Receiving stats
                    int recYds = Integer.parseInt(currentStats.getString("recYds"));
                    int recTD = Integer.parseInt(currentStats.getString("recTD"));
                    int receptions = Integer.parseInt(currentStats.getString("receptions"));

                    updateSeasonStat("RecYds", recYds);
                    updateSeasonStat("RecTD", recTD);
                    updateSeasonStat("Receptions", receptions);

                    if (thisWeekGame){
                        playerStats.put("weekRecYds", recYds);
                        playerStats.put("weekRecTD", recTD);
                        playerStats.put("weekReceptions", receptions);
                    }

                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Rushing"); //Rushing stats
                    int rushYds = Integer.parseInt(currentStats.getString("rushYds"));
                    int rushTD = Integer.parseInt(currentStats.getString("rushTD"));

                    updateSeasonStat("RushYds", rushYds);
                    updateSeasonStat("RushTD", rushTD);

                    if (thisWeekGame){
                        playerStats.put("weekRushYds", rushYds);
                        playerStats.put("weekRushTD", rushTD);
                    }

                } catch (JSONException ignored) {}

                try {
                    currentStats = game.getJSONObject("Passing"); //Passing stats
                    int passYds = Integer.parseInt(currentStats.getString("passYds"));
                    int passTD = Integer.parseInt(currentStats.getString("passTD"));
                    int interceptions = Integer.parseInt(currentStats.getString("int"));

                    updateSeasonStat("PassYds", passYds);
                    updateSeasonStat("PassTD", passTD);
                    updateSeasonStat("Interceptions", interceptions);

                    if(thisWeekGame){
                        playerStats.put("weekPassYds", passYds);
                        playerStats.put("weekPassTD", passTD);
                        playerStats.put("weekInterceptions", interceptions);
                    }

                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Kicking"); //Kicking stats
                    int fgMade = Integer.parseInt(currentStats.getString("fgMade"));
                    int fgAttempts = Integer.parseInt(currentStats.getString("fgAttempts"));
                    int xpMade = Integer.parseInt(currentStats.getString("xpMade"));
                    int xpAttempts = Integer.parseInt(currentStats.getString("xpAttempts"));

                    updateSeasonStat("FgMade", fgMade);
                    updateSeasonStat("FgAttempts", fgAttempts);
                    updateSeasonStat("XpMade", xpMade);
                    updateSeasonStat("XpAttempts", xpAttempts);

                    if(thisWeekGame){
                        playerStats.put("weekFgMade", fgMade);
                        playerStats.put("weekFgAttempts", fgAttempts);
                        playerStats.put("weekXpMade", xpMade);
                        playerStats.put("weekXpAttempts", xpAttempts);
                    }

                } catch (JSONException ignored) {}

                try{
                    currentStats = game.getJSONObject("Defense"); //For fumbles
                    int fumbles = Integer.parseInt(currentStats.getString("fumblesLost"));

                    updateSeasonStat("Fumbles", fumbles);

                    if(thisWeekGame){
                        playerStats.put("weekFumbles", fumbles);
                    }

                } catch (JSONException ignored) {}
            }
            else {
                break;
            }
        }
    }

    private void updateSeasonStat(String stat, int updateValue){
        stat = String.format("season%s", stat);
        playerStats.put(stat, playerStats.get(stat) + updateValue);
    }

    private String getStatsFromAPI() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com/getNFLGamesForPlayer?playerID=%s&itemFormat=list&numberOfGames=20", playerID)))
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
