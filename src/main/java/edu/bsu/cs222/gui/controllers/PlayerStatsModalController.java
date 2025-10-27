package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

import java.awt.*;

public class PlayerStatsModalController {
    @FXML private Label playerLabel;
    @FXML private Label tabLbl;

    private Player player;

    public void setPlayer(Player player){
        this.player = player;
        viewPlayerStats();
    }

    private void viewPlayerStats(){
        if(player != null){
            playerLabel.setText(player.getName());
            tabLbl.setText("Season");
        }
    }

}
