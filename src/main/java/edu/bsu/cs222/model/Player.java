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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private int receivingYd;
    private int receivingTD;
    private int receptions;
    private int rushYd;
    private int rushTD;
    private int passYd;
    private int passTD;
    private int passAtt;
    private int completions;
    private int fumbles;
    private int interceptions;
    public Map <String, Integer> playerStats = new HashMap<>();

    private int fieldGoalsMade;
    private int fieldGoalAttempts;
    private int extraPointsMade;
    private int extraPointAttempts;


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

    public Player (String name, String playerID)
    {
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

    public void setReceivingTD(int receivingTD) {
        this.receivingTD = receivingTD;
    }


    public void setReceivingYd(int receivingYd) {
        this.receivingYd = receivingYd;
    }

    public void setReceptions(int receptions) {
        this.receptions = receptions;
    }


    public void setRushYd(int rushYd) {
        this.rushYd = rushYd;
    }


    public void setRushTD(int rushTD) {
        this.rushTD = rushTD;
    }

    public void setPassYd(int passYd) {
        this.passYd = passYd;
    }

    public void setPassTD(int passTD) {
        this.passTD = passTD;
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

    public double getScore(){
        return ((this.rushYd+this.receivingYd) * 0.1 + (this.rushTD+this.receivingTD)*7
                + this.passTD * 4 + this.passYd *0.04 + this.receptions - this.interceptions*2 - this.fumbles*2
        - this.extraPointAttempts + this.extraPointsMade*2 - this.fieldGoalAttempts + this.fieldGoalsMade*4);
    }


    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }
    public void setExtraPointsMade(int extraPointsMade) {
        this.extraPointsMade = extraPointsMade;
    }

    public void setFieldGoalsMade(int fieldGoalsMade) {
        this.fieldGoalsMade = fieldGoalsMade;
    }

    public void setFieldGoalAttempts(int fieldGoalAttempts) {
        this.fieldGoalAttempts = fieldGoalAttempts;
    }

    public void setExtraPointAttempts(int extraPointAttempts) {
        this.extraPointAttempts = extraPointAttempts;
    }


    public void setFumbles(int fumbles) {
        this.fumbles = fumbles;
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



    public String getWeeklyStatsFromAPI() throws InterruptedException {
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
    public void setPlayerStats(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData).getJSONArray("body").getJSONObject(0);
        JSONObject currentStats;
        try {
            currentStats = jsonObject.getJSONObject("Receiving"); //Receiving stats
            playerStats.put("recYds", Integer.parseInt(currentStats.getString("recYds")));
            playerStats.put("recTD", Integer.parseInt(currentStats.getString("recTD")));
            playerStats.put("receptions", Integer.parseInt(currentStats.getString("receptions")));
            playerStats.put("targets", Integer.parseInt(currentStats.getString("targets")));
            try {
                playerStats.put("receivingTwoPointConversion", Integer.parseInt(currentStats.getString("receivingTwoPointConversion")));
            }
            catch (JSONException e){
                playerStats.put("receivingTwoPointConversion", 0);
            }
        }
        catch (JSONException e){
            currentStats = null;
            playerStats.put("recYds", 0);
            playerStats.put("recTD", 0);
            playerStats.put("receptions", 0);
            playerStats.put("targets", 0);
            playerStats.put("receivingTwoPointConversion", 0);
        }
        try {
            currentStats = jsonObject.getJSONObject("Rushing"); //Rushing stats
            playerStats.put("rushYds", Integer.parseInt(currentStats.getString("rushYds")));
            playerStats.put("rushTD", Integer.parseInt(currentStats.getString("rushTD")));
            playerStats.put("carries", Integer.parseInt(currentStats.getString("carries")));
            try {
                playerStats.put("rushingTwoPointConversion", Integer.parseInt(currentStats.getString("rushingTwoPointConversion")));
            }
            catch (JSONException e){
                playerStats.put("rushingTwoPointConversion", 0);
            }
        }
        catch (JSONException e){
            currentStats = null;
            playerStats.put("rushYds", 0);
            playerStats.put("rushTD", 0);
            playerStats.put("carries", 0);
            playerStats.put("rushingTwoPointConversion", 0);
        }
        try {
            currentStats = jsonObject.getJSONObject("Passing"); //Passing stats
            playerStats.put("passAttempts", Integer.parseInt(currentStats.getString("passAttempts")));
            playerStats.put("passCompletions", Integer.parseInt(currentStats.getString("passCompletions")));
            playerStats.put("passYds", Integer.parseInt(currentStats.getString("passYds")));
            playerStats.put("passTD", Integer.parseInt(currentStats.getString("passTD")));
            playerStats.put("int", Integer.parseInt(currentStats.getString("int")));
            try {
                playerStats.put("passingTwoPointConversion", Integer.parseInt(currentStats.getString("passingTwoPointConversion")));
            }
            catch (JSONException e){
                playerStats.put("passingTwoPointConversion", 0);
            }
        }
        catch (JSONException e){
            currentStats = null;
            playerStats.put("passAttempts", 0);
            playerStats.put("passCompletions", 0);
            playerStats.put("passYds", 0);
            playerStats.put("passTD", 0);
            playerStats.put("int", 0);
            playerStats.put("passingTwoPointConversion", 0);
        }
        try{
            currentStats = jsonObject.getJSONObject("Kicking"); //Passing stats
            playerStats.put("fgMade", Integer.parseInt(currentStats.getString("fgMade")));
            playerStats.put("fgAttempts", Integer.parseInt(currentStats.getString("fgAttempts")));
            playerStats.put("xpMade", Integer.parseInt(currentStats.getString("xpMade")));
            playerStats.put("xpAttempts", Integer.parseInt(currentStats.getString("xpAttempts")));
        }
        catch (JSONException e){
            currentStats = null;
            playerStats.put("fgMade", 0);
            playerStats.put("fgAttempts", 0);
            playerStats.put("xpMade", 0);
            playerStats.put("xpAttempts", 0);
        }
        try{
            currentStats = jsonObject.getJSONObject("Defense"); //For fumbles
            playerStats.put("fumblesLost", Integer.parseInt(currentStats.getString("fumblesLost")));
        }
        catch (JSONException e){
            currentStats = null;
            playerStats.put("fumblesLost", 0);
        }
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
