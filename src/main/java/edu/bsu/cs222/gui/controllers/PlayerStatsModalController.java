package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.gui.ErrorModal;
import edu.bsu.cs222.gui.list_cells.PlayerCompareCell;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.PlayerRetriever;
import edu.bsu.cs222.model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.util.*;

public class PlayerStatsModalController {

    @FXML private SplitPane splitPane;

    @FXML private Label playerLabel;
    @FXML private Label tabLbl;
    @FXML private Button cancelButton;
    @FXML private Button seasonStatsBtn;
    @FXML private Button weeklyStatsBtn;
    @FXML private Button compareButton;

    @FXML private VBox playerSelectPanel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> positionFilter;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ListView<Player> listView;

    @FXML private Label passYdsValueLbl;
    @FXML private Label passTdValueLbl;
    @FXML private Label interceptionsValueLbl;
    @FXML private Label rushYdsValueLbl;
    @FXML private Label rushTdValueLbl;
    @FXML private Label recYdsValueLbl;
    @FXML private Label receptionsValueLbl;
    @FXML private Label fgMadeValueLbl;
    @FXML private Label fgAttemptsValueLbl;
    @FXML private Label xpMadeValueLbl;
    @FXML private Label xpAttemptsValueLbl;
    @FXML private Label fumblesValueLbl;
    @FXML private Label tdValueLbl;

    private String currentStatView = "Season Stats";
    private Player player;
    private boolean isComparePanelVisible = false;
    private boolean isComparing = false;
    private Player comparePlayer;

    @FXML
    private void initialize() {
        listView.setFixedCellSize(25);
        listView.setCellFactory(lv -> new PlayerCompareCell(this));

        positionFilter.setValue("All");
        teamFilter.setValue("All");

        ArrayList<Player> players = PlayerRetriever.getPlayerArrayList();
        managePlayerCompareView(players);
    }

    public void setPlayer(Player player) throws InterruptedException, IOException {
        this.player = player;

        boolean networkError = player.setStatsWithAPI();
        if (networkError){
            ErrorModal.throwErrorModal("Network Error", null);
            return;
        }

        updateStatsLabels();
    }

    private void updateStatsLabels() {
        if (player == null) return;

        playerLabel.setText(player.getName());
        tabLbl.setText(currentStatView);

        Map<String, Integer> stats = player.getPlayerStats();
        String prefix = currentStatView.equals("Season Stats") ? "season" : "week";

        passYdsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "PassYds", 0)));
        passTdValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "PassTD", 0)));
        interceptionsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "Interceptions", 0)));
        rushYdsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "RushYds", 0)));
        rushTdValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "RushTD", 0)));
        recYdsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "RecYds", 0)));
        receptionsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "Receptions", 0)));
        fgMadeValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "FgMade", 0)));
        fgAttemptsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "FgAttempts", 0)));
        xpMadeValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "XpMade", 0)));
        xpAttemptsValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "XpAttempts", 0)));
        fumblesValueLbl.setText(String.valueOf(stats.getOrDefault(prefix + "Fumbles", 0)));

        tdValueLbl.setText(String.valueOf(
                stats.getOrDefault(prefix + "RecTD", 0) + stats.getOrDefault(prefix + "RushTD", 0)
        ));
    }

    private void managePlayerCompareView(ArrayList<Player> players) {
        players.remove(player);

        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);

        setPositionAndTeams(players);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player p) -> {
            if (p == null) return false;

            // Search
            String searchText = searchField.getText();
            ArrayList<String> queries = (searchText == null || searchText.isBlank())
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));

            if (!runSearch(queries, p)) return false;

            // Position filter
            String positionString = positionFilter.getValue();
            if (!positionString.equals("All") && !positionString.isBlank()) {
                if (p.getPosition() != Position.valueOf(positionString)) return false;
            }

            // Team filter
            String teamString = teamFilter.getValue();
            if (!teamString.equals("All") && !teamString.isBlank()) {
                if (!teamString.equals(p.getTeam())) return false;
            }

            return true;
        }, searchField.textProperty(), teamFilter.valueProperty(), positionFilter.valueProperty()));
    }

    private boolean runSearch(ArrayList<String> queries, Player player) {
        for (String query : queries) {
            if (!player.getName().toLowerCase().contains(query)) return false;
        }
        return true;
    }

    private void setPositionAndTeams(ArrayList<Player> players) {
        Set<String> teams = new TreeSet<>();
        for (Player p : players) teams.add(p.getTeam());

        positionFilter.getItems().setAll("All");
        for (Position position : Position.values()) {
            if (position != Position.FLEX) positionFilter.getItems().add(position.toString());
        }

        teamFilter.getItems().setAll("All");
        teamFilter.getItems().addAll(teams);
    }

    @FXML
    private void setSeasonStatView() {
        currentStatView = "Season Stats";
        updateStatsLabels();
    }

    @FXML
    private void setWeeklyStatView() {
        currentStatView = "Weekly Stats";
        updateStatsLabels();
    }

    @FXML
    private void toggleComparePanel() {
        isComparePanelVisible = !isComparePanelVisible;
        playerSelectPanel.setVisible(isComparePanelVisible);
        playerSelectPanel.setManaged(isComparePanelVisible);

        compareButton.setText(isComparePanelVisible ? "Hide" : "Compare");
    }

    public void showComparePlayer(Player playerToCompare) throws InterruptedException, IOException {
        if (playerToCompare == null) return;

        comparePlayer = playerToCompare;
        isComparing = true;

        playerSelectPanel.setVisible(false);
        playerSelectPanel.setManaged(false);
        compareButton.setText("Compare");

        setPlayer(comparePlayer);
    }

    public void compareStats(Player comparedPlayer) throws InterruptedException, IOException {
        boolean networkError = comparedPlayer.setStatsWithAPI();
        if (networkError) {
            ErrorModal.throwErrorModal("Network Error", null);
            return;
        }

        Map<String, Integer> comparedStats = comparedPlayer.getPlayerStats();
        // Future: update UI for side-by-side comparison
    }
}
