package edu.bsu.cs222.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static edu.bsu.cs222.model.Position.*;

public class League {
    private final String name;
    private ArrayList<Position> teamPositions;
    private HashMap<String, Double> coefficientMap;

    private boolean saved = false;

    private final ArrayList<Team> teams = new ArrayList<>();

    public League(String name, ArrayList<Position> teamPositions, HashMap<String, Double> coefficientMap){
        this.name = name;
        this.teamPositions = teamPositions;
        this.coefficientMap = coefficientMap;
    }

    public League(String jsonData){
        saved = true;
        JSONObject leagueObject = new JSONObject(jsonData);
        name = leagueObject.getString("leagueName");

        teamPositions = new ArrayList<>();
        JSONObject positionsObject = leagueObject.getJSONObject("positions");
        for(String key : positionsObject.keySet()){
            for (int i = 0; i < positionsObject.getInt(key); ++i){
                teamPositions.add(valueOf(key));
            }
        }

        coefficientMap = new HashMap<>();
        JSONObject scoringObject = leagueObject.getJSONObject("scoring");
        for (String key: scoringObject.keySet()){
            coefficientMap.put(key, scoringObject.getDouble(key));
        }

        JSONArray teamsArray = leagueObject.getJSONArray("teams");
        for (int i = 0; i < teamsArray.length(); ++i){
            JSONObject teamObject = teamsArray.getJSONObject(i);
            Team team = new Team(teamObject.getString("teamName"), teamPositions, coefficientMap);

            JSONArray playersArray = teamObject.getJSONArray("players");
            for (int j = 0; j < playersArray.length(); ++j){
                JSONObject playerObject = playersArray.getJSONObject(j);
                HashMap<String, String> playerInfo = new HashMap<>();
                for (String key: playerObject.keySet()){
                    playerInfo.put(key, playerObject.getString(key));
                }
                team.addPlayer(new Player(playerInfo), Position.valueOf(playerInfo.get("position")));
            }
            this.addTeam(team);
        }
    }

    public void addTeam(String teamName){
        teams.add(new Team(teamName, teamPositions, coefficientMap));
    }

    private void addTeam(Team team){
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }

    public void setTeamPositions(ArrayList<Position> teamPositions, boolean lessPositions){
        this.teamPositions = teamPositions;

        if(lessPositions){
            for (Team team: teams){
                team.removeExtraPlayers(teamPositions);
            }
        }else {
            for (Team team: teams){
                team.setFreePositions(teamPositions);
            }
        }
    }

    public void setCoefficientMap(HashMap<String, Double> coefficientMap){
        this.coefficientMap = coefficientMap;
        for (Team team: teams){
            team.setCoefficientMap(coefficientMap);
        }
    }

    public void saveLeague(){
        saved = true;
        JSONObject leagueObject = new JSONObject();
        leagueObject.put("leagueName", name);

        JSONArray teamsArray = new JSONArray();

        for (Team team: teams){
            JSONObject teamObject = new JSONObject();
            teamObject.put("teamName", team.getName());

            JSONArray playersArray = new JSONArray();

            for (Player player: team.getPlayerMap().keySet()){
                JSONObject playerObject = new JSONObject();

                playerObject.put("playerID", player.getPlayerID());
                playerObject.put("playerName", player.getNonScoringStats().get("name"));
                playerObject.put("position", team.getPlayerMap().get(player).toString());
                playerObject.put("team", player.getNonScoringStats().get("team"));
                playerObject.put("school", player.getNonScoringStats().get("school"));
                playerObject.put("jerseyNumber", player.getNonScoringStats().get("jerseyNumber"));
                playerObject.put("experience", player.getNonScoringStats().get("experience"));
                playerObject.put("weight", player.getNonScoringStats().get("weight"));
                playerObject.put("height", player.getNonScoringStats().get("height"));
                playerObject.put("headshot", player.getNonScoringStats().get("headshot"));

                playersArray.put(playerObject);
            }

            teamObject.put("players", playersArray);
            teamsArray.put(teamObject);
        }
        leagueObject.put("teams", teamsArray);

        HashMap<Position, Integer> positionCountMap = getPositionCountMap();
        JSONObject positionsObject = new JSONObject();
        for (Position position: positionCountMap.keySet()){
            positionsObject.put(position.toString(), positionCountMap.get(position));
        }
        leagueObject.put("positions", positionsObject);

        JSONObject scoringObject = new JSONObject();
        for (String key: coefficientMap.keySet()){
            scoringObject.put(key, coefficientMap.get(key));
        }
        leagueObject.put("scoring", scoringObject);

        Path savedLeaguesDir = Paths.get("SavedFiles/SavedLeagues");
        if(Files.notExists(savedLeaguesDir)){
            try {
                Files.createDirectories(savedLeaguesDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String filePath = String.format("SavedFiles/SavedLeagues/%s.json", getFileSafeName());
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writer.write(leagueObject.toString(4));
        } catch (IOException e) {
            System.err.println("Couldn't write to file");
        }
    }

    public void deleteLeague() {
        String filePath = String.format("SavedFiles/SavedLeagues/%s.json", getFileSafeName());
        try {
            Files.delete(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Getters
    public  ArrayList<Position> getTeamPositions() {
        Collections.sort(teamPositions);
        return teamPositions;
    }

    public HashMap<Position, Integer> getPositionCountMap() {
        int currentQb = 0;
        int currentRb = 0;
        int currentTe = 0;
        int currentWr = 0;
        int currentK = 0;
        int currentFlex = 0;
        for (Position position : teamPositions) {
            switch (position) {
                case QB:
                    ++currentQb;
                    break;
                case RB:
                    ++currentRb;
                    break;
                case TE:
                    ++currentTe;
                    break;
                case WR:
                    ++currentWr;
                    break;
                case K:
                    ++currentK;
                    break;
                default:
                    ++currentFlex;
                    break;
            }
        }

        HashMap<Position, Integer> positionCountMap = new HashMap<>();
        positionCountMap.put(QB, currentQb);
        positionCountMap.put(RB, currentRb);
        positionCountMap.put(TE, currentTe);
        positionCountMap.put(WR, currentWr);
        positionCountMap.put(K, currentK);
        positionCountMap.put(FLEX, currentFlex);
        return positionCountMap;
    }

    public HashMap<String, Double> getCoefficientMap() {
        return coefficientMap;
    }

    public Team getTeamByName(String name){
        for(Team team: teams){
            if(team.getName().equals(name)){
                return team;
            }
        }
        return null;
    }

    public ArrayList<String> getTeamNames(){
        ArrayList<String> teamNames = new ArrayList<>();
        for (Team team: teams){
            teamNames.add(team.getName());
        }
        return teamNames;
    }

    public boolean getSaved(){return saved;}

    public String getName() {
        return name;
    }

    public String getFileSafeName(){
        String INVALID_CHARACTERS = "[/:*?\"<>|\\\\ ]";
        return name.trim().replaceAll(INVALID_CHARACTERS, "_");
    }


    //Internal team class
    public static class Team {
        private final String teamName;
        private ArrayList<Position> freePositions;
        private HashMap<String, Double> coefficientMap;

        private final HashMap<Player, Position> playerMap = new HashMap<>();
        private final ArrayList<String> playerNameList = new ArrayList<>();
        private double calculatedScore = -1;

        private Team(String teamName, ArrayList<Position> positions, HashMap<String, Double> coefficientMap) {
            this.teamName = teamName;
            this.freePositions = new ArrayList<>(positions);
            this.coefficientMap = coefficientMap;
        }

        //Player Functions
        public void addPlayer(Player player, Position position){
            playerMap.put(player, position);
            freePositions.remove(position);
            playerNameList.add(player.getNonScoringStats().get("name"));
        }

        public void removePlayer(Player player){
            freePositions.add(playerMap.get(player));
            playerMap.remove(player);
            playerNameList.remove(player.getNonScoringStats().get("name"));
        }

        private void removeExtraPlayers(ArrayList<Position> freePositions){
            this.freePositions = freePositions;
            ArrayList<Player> playersToRemove = new ArrayList<>();
            for(Player key: playerMap.keySet()){
                if(!freePositions.remove(playerMap.get(key))){
                    playersToRemove.add(key);
                }
            }
            for(Player player: playersToRemove){
                playerMap.remove(player);
            }
        }


        //Setters
        private void setFreePositions(ArrayList<Position> freePositions){
            this.freePositions = freePositions;

            for (Player key: playerMap.keySet()){
                freePositions.remove(playerMap.get(key));
            }
        }

        private void setCoefficientMap(HashMap<String, Double> coefficientMap){
            this.coefficientMap = coefficientMap;
        }

        //Getters
        public double getCalculatedScore() {
            return calculatedScore;
        }

        public HashMap<Player, Position> getPlayerMap(){
            return playerMap;
        }

        public ArrayList<String> getPlayerNameList(){
            return playerNameList;
        }

        public ArrayList<Position> getFreePositions(){
            Collections.sort(freePositions);
            return freePositions;
        }

        public HashMap<String, Double> getCoefficientMap() {
            return coefficientMap;
        }

        public String getName(){
            return teamName;
        }

        //Setter for tests
        public void setCalculatedScore(double calculatedScore) {
            this.calculatedScore = calculatedScore;
        }
    }
}
