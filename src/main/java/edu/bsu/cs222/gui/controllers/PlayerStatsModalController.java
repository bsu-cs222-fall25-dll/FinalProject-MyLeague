package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.gui.list_cells.PlayerCompareCell;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.PlayerRetriever;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class PlayerStatsModalController {
    public Button cancelButton;
    public Button seasonStatsBtn;
    public Button weeklyStatsBtn;
    public SplitPane splitPane;
    @FXML private ListView<Player> listView;
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
    private void initialize() {
        listView.setFixedCellSize(25);
        listView.setCellFactory(lv -> new PlayerCompareCell());
        //positionFilter.setValue("All");
        //teamFilter.setValue("All");

        managePlayerCompareView(PlayerRetriever.getPlayerArrayList());
    }

    private void managePlayerCompareView(ArrayList<Player> players) {
        players.remove(player);
        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);
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
