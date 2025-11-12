package edu.bsu.cs222.model;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private static final String API_KEY = Dotenv.load().get("API_KEY");
    private String name;
    private String shortName;
    private Position position;
    private String team;
    private String jerseyNumber;
    private String height;
    private String weight;
    private String age;
    private String headshot;
    private JSONObject injury;
    private String school;
    private String playerID;
    private String teamID;
    private String experience;
    private String bDay;
    //Above are stats shown from player list, below are stats which require a deeper API call.
    private int passAtt;
    private int completions;
    private LocalDate lastScoreDate;
    public HashMap <String, Integer> playerStats = new HashMap<>();


    public Player(String name, Position position, String team, String jerseyNumber, String height,
                  String weight, String age, String bDay, String headshot, JSONObject injury, String school,
                  String playerID, String teamID, String experience) {
        this.name = name;
        this.position = position;
        this.team = team;
        this.jerseyNumber = jerseyNumber;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.headshot = headshot;
        this.injury = injury;
        this.school = school;
        this.playerID = playerID;
        this.teamID = teamID;
        this.experience = experience;
        this.bDay = bDay;

        this.shortName = name.charAt(0) + ". " + name.split(" ")[1];
    }

    public Player(String name){
        this.name = name;
        this.shortName = name.charAt(0) + ". " + name.split(" ")[1];
    }

    public Player (String name, String playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public Player(){}

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public String getTeam() {
        return team;
    }

    public String getJerseyNumber() {
        return jerseyNumber;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getAge() {
        return age;
    }

    public String getHeadshot() {
        return headshot;
    }

    public String getSchool() {
        return school;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getExperience() {
        return experience;
    }

    public void setPassAtt(int passAtt) {
        this.passAtt = passAtt;
    }

    public void setCompletions(int completions) {
        this.completions = completions;
    }

    public double getCompletionPCT(){
        double compPCT = (double)this.completions / this.passAtt;
        return  (Math.round(compPCT *1000) / 1000.0);
    }

    public double getWeekScore(){
        return ((playerStats.get("weekRushYds")+playerStats.get("weekRecYds")) * 0.1 +
                (playerStats.get("weekRushTD")+playerStats.get("weekRecTD"))*7
                + playerStats.get("weekPassTD") * 4 + playerStats.get("weekPassYds") *0.04 +
                playerStats.get("weekReceptions") - playerStats.get("weekInterceptions")*2 - playerStats.get("weekFumbles")*2
        - playerStats.get("weekXpAttempts") + playerStats.get("weekXpMade")*2 -
                playerStats.get("weekFgAttempts") + playerStats.get("weekFgMade")*4);
    }

    

    public String getShortName() {
        return shortName;
    }

    public JSONObject getInjury() {
        return injury;
    }

    public String getbDay() {
        return bDay;
    }



    public String getWeekStatsFromAPI() throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com/getNFLGamesForPlayer?playerID="+playerID+"&itemFormat=list&numberOfGames=1"))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", "tank01-nfl-live-in-game-real-time-statistics-nfl.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch (IOException e){
            //Can't truly be tested
            return "Network Error";
        }
    }

    public void setPlayerStats(HashMap<String, Integer> playerStats){
        this.playerStats = playerStats;
    }

    public void setPlayerStats(String jsonData){
        JSONArray playerGames = new JSONObject(jsonData).getJSONArray("body");
        JSONObject recentGame = playerGames.getJSONObject(0);

        LocalDate today = LocalDate.now();
        lastScoreDate = today;
        JSONObject currentStats;
        LocalDate recentGameDate = LocalDate.parse(recentGame.getString("gameID").substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));

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

        if (recentGameDate.isAfter(today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)))){
            try {
                currentStats = recentGame.getJSONObject("Receiving"); //Receiving stats
                playerStats.put("weekRecYds", Integer.parseInt(currentStats.getString("recYds")));
                playerStats.put("weekRecTD", Integer.parseInt(currentStats.getString("recTD")));
                playerStats.put("weekReceptions", Integer.parseInt(currentStats.getString("receptions")));
            } catch (JSONException ignored) {}

            try {
                currentStats = recentGame.getJSONObject("Rushing"); //Rushing stats
                playerStats.put("weekRushYds", Integer.parseInt(currentStats.getString("rushYds")));
                playerStats.put("weekRushTD", Integer.parseInt(currentStats.getString("rushTD")));
            } catch (JSONException ignored) {}

            try {
                currentStats = recentGame.getJSONObject("Passing"); //Passing stats
                playerStats.put("weekPassYds", Integer.parseInt(currentStats.getString("passYds")));
                playerStats.put("weekPassTD", Integer.parseInt(currentStats.getString("passTD")));
                playerStats.put("weekInterceptions", Integer.parseInt(currentStats.getString("int")));
            } catch (JSONException ignored) {}

            try{
                currentStats = recentGame.getJSONObject("Kicking"); //Passing stats
                playerStats.put("weekFgMade", Integer.parseInt(currentStats.getString("fgMade")));
                playerStats.put("weekFgAttempts", Integer.parseInt(currentStats.getString("fgAttempts")));
                playerStats.put("weekXpMade", Integer.parseInt(currentStats.getString("xpMade")));
                playerStats.put("weekXpAttempts", Integer.parseInt(currentStats.getString("xpAttempts")));
            } catch (JSONException ignored) {}

            try{
                currentStats = recentGame.getJSONObject("Defense"); //For fumbles
                playerStats.put("weekFumbles", Integer.parseInt(currentStats.getString("fumblesLost")));
            } catch (JSONException ignored) {}
        }

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

        int seasonYear = today.getMonthValue() >= Month.SEPTEMBER.getValue() ? today.getYear() : today.getYear() - 1;
        LocalDate seasonStart = LocalDate.of(seasonYear, Month.SEPTEMBER, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        for (Object gameObject : playerGames){
            JSONObject game = (JSONObject) gameObject;
            LocalDate gameDate = LocalDate.parse(game.getString("gameID").substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
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
                return;
            }
        }
    }

    public LocalDate getLastScoreDate() {
        return lastScoreDate;
    }

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
