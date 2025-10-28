package edu.bsu.cs222;

import org.json.JSONObject;

import java.util.Objects;

public class Player {
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
    private int rushAtt;
    private int passYd;
    private int passTD;
    private int passAtt;
    private int completions;
    private int fumbles;
    private int interceptions;
    private String matchup;

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
