package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class PlayerStatsModalController {
    public Button cancelButton;
    public Button seasonStatsBtn;
    public Button weeklyStatsBtn;
    @FXML private Label playerLabel;
    @FXML private Label tabLbl;
    String currentStatView = "Season";
    private Player player;

    public void setPlayer(Player player){
        this.player = player;
        viewPlayerStats();
    }

    private void viewPlayerStats(){
        if(player != null){
            playerLabel.setText(player.getName());
            tabLbl.setText(currentStatView);
        }
    }

    @FXML
    private void setSeasonStatView(){
        currentStatView = "Season";
        tabLbl.setText(currentStatView);
    }

    @FXML
    private void setWeeklyStatView(){
        currentStatView = "Weekly";
        tabLbl.setText(currentStatView);
    }
}
