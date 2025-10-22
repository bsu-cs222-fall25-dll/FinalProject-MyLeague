package edu.bsu.cs222;

import edu.bsu.cs222.gui.controllers.Position;

import java.util.ArrayList;

public class Draft {
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Position> teamPositions;
    private String title;

    public Draft(String title, ArrayList<Position> teamPositons){
        this.title = title;
        this.teamPositions = teamPositons;
    }

    public void addTeam(Team team){
        teams.add(team);
    }

    public  ArrayList<Position> getTeamPositions() {
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

    public String getTitle() {
        return title;
    }
}
