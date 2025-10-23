package edu.bsu.cs222;

import edu.bsu.cs222.gui.controllers.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class League {
    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Position> teamPositions;
    private final String name;

    public League(String name, ArrayList<Position> teamPositons){
        this.name = name;
        this.teamPositions = teamPositons;
    }

    public void addTeam(String teamName){
        teams.add(new Team(teamName, teamPositions));
    }

    public  ArrayList<Position> getTeamPositions() {
        Collections.sort(teamPositions);
        return teamPositions;
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

    public static class Team {
        private final String teamName;
        private final HashMap<Player, Position> playerMap = new HashMap<>();
        private final ArrayList<Position> freePositions;

        private Team(String teamName, ArrayList<Position> positions) {
            this.teamName = teamName;
            freePositions = positions;
        }

        public void addPlayer(Player player, Position position){
            playerMap.put(player, position);
            freePositions.remove(position);
        }

        public ArrayList<Position> getFreePositions(){
            Collections.sort(freePositions);
            return freePositions;
        }

        public void removePlayer(Player player){
            freePositions.add(playerMap.get(player));
            playerMap.remove(player);
        }

        public String getName(){
            return teamName;
        }

        public HashMap<Player, Position> getPlayerMap(){
            return playerMap;
        }
    }
}
