package edu.bsu.cs222;

import edu.bsu.cs222.gui.controllers.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Team {
    private String teamName;
    private HashMap<Player, Position> playerMap = new HashMap<>();
    private ArrayList<Position> freePositions;

    public Team(String teamName, Draft draft) {
        this.teamName = teamName;
        freePositions = draft.getTeamPositions();
        draft.addTeam(this);
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
}
