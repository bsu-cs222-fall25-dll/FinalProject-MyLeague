package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerCompareCellController {
    @FXML private Label detailsLbl;
    @FXML private Label nameLbl;

    public void setData(Player player) {
        nameLbl.setText(player.getName());
    }
}
