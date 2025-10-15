package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.PlayerCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayersViewController {
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;
    private FilteredList<Player> filteredList;
    private final String[] positionArray = new String[] {"All", "QB"};
    private final String[] teamArray = new String[] {"All", "ARI"};

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayerCell());

        positionFilter.getItems().addAll(positionArray);
        teamFilter.getItems().addAll(teamArray);

        positionFilter.setValue("All");
        teamFilter.setValue("All");

        positionFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredList.setPredicate(player -> {
                    if ("All".equals(newVal)) {return true;}
                    return player.getPosition().equalsIgnoreCase(newVal);
                }));

        teamFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredList.setPredicate(player -> {
                    if ("All".equals(newVal)) {return true;}
                    return player.getTeam().equalsIgnoreCase(newVal);
                }));
    }

    public void setPlayers(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);

        searchField.textProperty().addListener((obsV, oldValue, newValue) -> {
            ArrayList<String> queries = (newValue == null || newValue.isBlank()) ?  new ArrayList<>(): new ArrayList<>(Arrays.asList(newValue.toLowerCase().split("\\s+")));
            filteredList.setPredicate(p -> queries.isEmpty() || runSearch(queries, p));
        });
    }

    private boolean runSearch(ArrayList<String> queries, Player player){
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
}
