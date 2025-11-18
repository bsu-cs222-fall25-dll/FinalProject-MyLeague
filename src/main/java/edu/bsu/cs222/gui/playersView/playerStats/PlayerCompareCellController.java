package edu.bsu.cs222.gui.playersView.playerStats;

import edu.bsu.cs222.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class PlayerCompareCellController {

    @FXML private Label nameLbl;
    @FXML private Label detailsLbl;

    private PlayerStatsModalController parent;
    private Player player;

    // Set data for this cell
    public void setData(Player player) {
        this.player = player;

        // Basic info
        nameLbl.setText(player.getName());
        detailsLbl.setText(player.getPosition().toString() + " • " + player.getTeam());
    }

    // Link back to the main stats modal controller
    public void setParentController(PlayerStatsModalController parent) {
        this.parent = parent;
    }

    // Handle click for comparison
    public void compareStats() throws InterruptedException, IOException {
        if (parent != null && player != null) {
            parent.showComparePlayer(player);
        }
    }
}
