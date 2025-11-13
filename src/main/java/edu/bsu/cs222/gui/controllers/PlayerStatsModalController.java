package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.gui.ErrorModal;
import edu.bsu.cs222.gui.list_cells.PlayerCompareCell;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.PlayerRetriever;
import edu.bsu.cs222.model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.util.*;

public class PlayerStatsModalController {
    public Button cancelButton;
    public Button seasonStatsBtn;
    public Button weeklyStatsBtn;
    public SplitPane splitPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> positionFilter;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ListView<Player> listView;
    @FXML private Label playerLabel;
    @FXML private Label tabLbl;
    @FXML private VBox playerSelectPanel;
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
        listView.setCellFactory(lv -> new PlayerCompareCell(this));
        positionFilter.setValue("All");
        teamFilter.setValue("All");

        managePlayerCompareView(PlayerRetriever.getPlayerArrayList());
    }

    private void managePlayerCompareView(ArrayList<Player> players) {
        players.remove(player);
        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);
        setPositionAndTeams(players);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player player) -> {
            String searchText = searchField.getText();
            ArrayList<String> queries = (searchText == null || searchText.isBlank()) ? new ArrayList<>(): new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));
            if (!runSearch(queries, player)) {return false;}

            String positionString = positionFilter.getValue();
            if (!positionString.isBlank() && !positionString.equals("All")){
                if(player.getPosition() != Position.valueOf(positionString)) {return false;}
            }

            String teamString = teamFilter.getValue();
            if (!teamString.isBlank() && !teamString.equals("All")){
                return teamString.equals(player.getTeam());
            }

            return true;

                },
                searchField.textProperty(),
                teamFilter.valueProperty(),
                positionFilter.valueProperty()));
    }

    private boolean runSearch(ArrayList<String> queries, Player player) {
        if (player == null) {return false;}
        boolean match = true;
        for (String query : queries){
            if (!player.getName().toLowerCase().contains(query)) {
                match = false;
                break;
            }
        }
        return match;
    }

    private void setPositionAndTeams(ArrayList<Player> players) {
        Set<String> teams = new TreeSet<>();
        for (Player player : players){
            String team = player.getTeam();
            teams.add(team);
        }

        positionFilter.getItems().add("All");
        teamFilter.getItems().add("All");

        for (Position position: Position.values()){
            positionFilter.getItems().add(position.toString());
        }
        positionFilter.getItems().remove("FLEX");

        teamFilter.getItems().addAll(teams);
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
        playerSelectPanel.setVisible(isComparePanelVisible);
        playerSelectPanel.setManaged(isComparePanelVisible);

        if (isComparePanelVisible) {
            compareButton.setText("Hide");
        }
        else {
            compareButton.setText("Compare");
        }
    }

    public void compareStats(Player comparedPlayer) throws InterruptedException, IOException {
        boolean networkError = comparedPlayer.setStatsWithAPI();
        if (networkError){
            ErrorModal.throwErrorModal("Network Error", null);
            return;
        }
        HashMap<String, Integer> comparedPlayerStats = comparedPlayer.getPlayerStats();
    }
}
