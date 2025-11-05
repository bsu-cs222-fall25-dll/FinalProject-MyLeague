package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.model.Player;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class PlayerStatsModalController {
    public Button cancelButton;
    public Button seasonStatsBtn;
    public Button weeklyStatsBtn;
    public SplitPane splitPane;
    @FXML private Label playerLabel;
    @FXML private Label tabLbl;
    @FXML private VBox comparePanel;
    @FXML private Button compareButton;

    String currentStatView = "Season Stats";
    private Player player;
    private boolean isComparePanelVisible = false;

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
        currentStatView = "Season Stats";
        tabLbl.setText(currentStatView);
    }

    @FXML
    private void setWeeklyStatView(){
        currentStatView = "Weekly Stats";
        tabLbl.setText(currentStatView);
    }

    @FXML
    private void toggleComparePanel() {
        isComparePanelVisible = !isComparePanelVisible;
        comparePanel.setVisible(isComparePanelVisible);
        comparePanel.setManaged(isComparePanelVisible);

        if (isComparePanelVisible) {
            compareButton.setText("Hide");
        }
        else {
            compareButton.setText("Compare");
        }
    }
}
