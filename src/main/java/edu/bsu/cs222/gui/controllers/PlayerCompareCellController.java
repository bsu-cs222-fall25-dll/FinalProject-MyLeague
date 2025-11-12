package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerCompareCellController {
    @FXML private Label detailsLbl;
    @FXML private Label nameLbl;
    private PlayerStatsModalController parent;
    private Player player;

    public void setData(Player player) {
        this.player = player;
        nameLbl.setText(player.getName());
        detailsLbl.setText(player.getPosition().toString() + " • " + player.getTeam());
    }

    public void setParentController(PlayerStatsModalController parent) {
        this.parent = parent;
    }

    public void compareStats() {
        parent.compareStats(player);
    }
}
