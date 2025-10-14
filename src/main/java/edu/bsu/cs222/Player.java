package edu.bsu.cs222;

import org.json.JSONObject;

public class Player {
    private String name;
    private String position;
    private String team;
    private int jerseyNumber;
    private int height;
    private int weight;
    private int age;
    private String headshot;
    private JSONObject injury;
    private String school;
    private String playerID;
    private String teamID;
    private int experience;
    private String bDay;
    //Above are stats shown from player list, below are stats which require a deeper API call.
    private int score;
    private int receivingYd;
    private int receivingTD;
    private int receptions;
    private int rushYd;
    private int rushTD;
    private int rushAtt;
    private int passYd;
    private int passTD;
    private int passAtt;
    private int completions;
    private int completionPct;
    private String matchup;

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
