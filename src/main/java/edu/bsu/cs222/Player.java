package edu.bsu.cs222;

import org.json.JSONObject;

import java.util.Objects;

public class Player {
    private String name;
    private String shortName;
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

    public int getExtraPointsMade() {
        return extraPointsMade;
    }

    public void setExtraPointsMade(int extraPointsMade) {
        this.extraPointsMade = extraPointsMade;
    }

    public int getFieldGoalsMade() {
        return fieldGoalsMade;
    }

    public void setFieldGoalsMade(int fieldGoalsMade) {
        this.fieldGoalsMade = fieldGoalsMade;
    }

    public int getFieldGoalAttempts() {
        return fieldGoalAttempts;
    }

    public void setFieldGoalAttempts(int fieldGoalAttempts) {
        this.fieldGoalAttempts = fieldGoalAttempts;
    }

    public int getExtraPointAttempts() {
        return extraPointAttempts;
    }

    public void setExtraPointAttempts(int extraPointAttempts) {
        this.extraPointAttempts = extraPointAttempts;
    }

    private int fieldGoalsMade;
    private int fieldGoalAttempts;
    private int extraPointsMade;
    private int extraPointAttempts;


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

        this.shortName = name.charAt(0) + ". " + name.split(" ")[1];
    }

    public Player(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(String jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public JSONObject getInjury() {
        return injury;
    }

    public void setInjury(JSONObject injury) {
        this.injury = injury;
    }

    public String getHeadshot() {
        return headshot;
    }

    public void setHeadshot(String headshot) {
        this.headshot = headshot;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getbDay() {
        return bDay;
    }

    public void setbDay(String bDay) {
        this.bDay = bDay;
    }

    public int getReceivingTD() {
        return receivingTD;
    }

    public void setReceivingTD(int receivingTD) {
        this.receivingTD = receivingTD;
    }

    public int getReceivingYd() {
        return receivingYd;
    }

    public void setReceivingYd(int receivingYd) {
        this.receivingYd = receivingYd;
    }

    public int getReceptions() {
        return receptions;
    }

    public void setReceptions(int receptions) {
        this.receptions = receptions;
    }

    public int getRushYd() {
        return rushYd;
    }

    public void setRushYd(int rushYd) {
        this.rushYd = rushYd;
    }

    public int getRushTD() {
        return rushTD;
    }

    public void setRushTD(int rushTD) {
        this.rushTD = rushTD;
    }

    public int getRushAtt() {
        return rushAtt;
    }

    public void setRushAtt(int rushAtt) {
        this.rushAtt = rushAtt;
    }

    public int getPassYd() {
        return passYd;
    }

    public void setPassYd(int passYd) {
        this.passYd = passYd;
    }

    public int getPassTD() {
        return passTD;
    }

    public void setPassTD(int passTD) {
        this.passTD = passTD;
    }

    public int getPassAtt() {
        return passAtt;
    }

    public void setPassAtt(int passAtt) {
        this.passAtt = passAtt;
    }

    public int getCompletions() {
        return completions;
    }

    public void setCompletions(int completions) {
        this.completions = completions;
    }

    public String getMatchup() {
        return matchup;
    }

    public void setMatchup(String matchup) {
        this.matchup = matchup;
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

    public int getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }

    public int getFumbles() {
        return fumbles;
    }

    public void setFumbles(int fumbles) {
        this.fumbles = fumbles;
    }

    public String getShortName() {
        return shortName;
    }
}
