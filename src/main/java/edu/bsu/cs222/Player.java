package edu.bsu.cs222;

import org.json.JSONObject;

public class Player {
    public String name;
    public String position;
    public String team;
    public int jerseyNumber;
    public int height;
    public int weight;
    public int age;
    public String headshot;
    public JSONObject injury;
    public String school;
    public String playerID;
    public String teamID;
    public int experience;
    public String bDay;
    //Above are stats shown from player list, below are stats which require a deeper API call.
    public int score;
    public int receivingYd;
    public int receivingTD;
    public int receptions;
    public int rushYd;
    public int rushTD;
    public int rushAtt;
    public int passYd;
    public int passTD;
    public int passAtt;
    public int completions;
    public int completionPct;
    public String matchup;

    public Player(String name, String position, String team, int jerseyNumber, int height,
                  int weight, int age, String bDay, String headshot, JSONObject injury, String school,
                  String playerID, String teamID, int experience) {
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
    }

}
