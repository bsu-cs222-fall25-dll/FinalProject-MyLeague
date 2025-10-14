package edu.bsu.cs222;

import org.json.JSONObject;

public class Player {
    private String name;
    private String position;
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

    public Player(String name, String position, String team, String jerseyNumber, String height,
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
    }

    public String getName() {
        return name;
    }
}
