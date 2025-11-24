package edu.bsu.cs222.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static edu.bsu.cs222.model.Position.*;

public class League {
    private final String name;
    private ArrayList<Position> teamPositions;
    private HashMap<String, Double> coefficientMap;

    private final ArrayList<Team> teams = new ArrayList<>();


    public League(String name, ArrayList<Position> teamPositions, HashMap<String, Double> coefficientMap){
        this.name = name;
        this.teamPositions = teamPositions;
        this.coefficientMap = coefficientMap;
    }

    public void addTeam(String teamName){
        teams.add(new Team(teamName, teamPositions, coefficientMap));
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

    public String getName() {
        return name;
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
            playerNameList.add(player.getName());
        }

        public void removePlayer(Player player){
            freePositions.add(playerMap.get(player));
            playerMap.remove(player);
            playerNameList.remove(player.getName());
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
