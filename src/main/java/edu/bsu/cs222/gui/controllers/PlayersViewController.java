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

import java.util.*;

public class PlayersViewController {
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;
    private FilteredList<Player> filteredList;

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayerCell());

        positionFilter.setValue("All");
        teamFilter.setValue("All");

        positionFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredList.setPredicate(player -> {
                    if ("All".equals(newVal)) {return true;}
                    if("None".equals(newVal)) {return player.getPosition().isBlank();}
                    return player.getPosition().equalsIgnoreCase(newVal);
                }));

        teamFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                filteredList.setPredicate(player -> {
                    if ("All".equals(newVal)) {return true;}
                    if("None".equals(newVal)) {return player.getTeam().isBlank();}
                    return player.getTeam().equalsIgnoreCase(newVal);
                }));
    }

    public void setPlayers(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);

        setPositionsAndTeams(players);

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

    public void setPositionsAndTeams(ArrayList<Player> players) {
        Set<String> positions = new TreeSet<>();
        Set<String> teams = new TreeSet<>();
        boolean blankPosition = false;
        boolean blankTeam = false;

        for (Player player : players){
            String position = player.getPosition();
            String team = player.getTeam();

            if (position.isBlank()){
                blankPosition = true;
            }
            else{
                positions.add(position);
            }

            if (team.isBlank()){
                blankTeam = true;
            }
            else{
                teams.add(team);
            }
        }

        positionFilter.getItems().add("All");
        teamFilter.getItems().add("All");

        positionFilter.getItems().addAll(positions);
        teamFilter.getItems().addAll(teams);

        if (blankPosition) {positionFilter.getItems().add("None");}
        if (blankTeam) {teamFilter.getItems().add("None");}
    }
}
