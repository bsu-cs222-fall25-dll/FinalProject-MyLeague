package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.PlayerCell;
import javafx.beans.binding.Bindings;
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

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayerCell());

        positionFilter.setValue("All");
        teamFilter.setValue("All");
    }

    public void setPlayers(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);

        setPositionsAndTeams(players);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player player) -> {
            String searchText = searchField.getText();
            ArrayList<String> queries = (searchText == null || searchText.isBlank()) ? new ArrayList<>(): new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));
            if (!runSearch(queries, player)) {return false;}

            String positionString = positionFilter.getValue();
            if (!positionString.isBlank()){
                if(!positionString.equals("All")){
                    if (player.getPosition() != Position.valueOf(positionString)) {return false;}
                }
            }

            String team = teamFilter.getValue();
            if (!team.isBlank()){
                if(!team.equals("All")){
                    if (team.equals("None")){
                        if (!player.getTeam().isBlank()) {return false;}
                    }
                    if (!team.equals(player.getTeam())) {return false;}
                }
            }
            return true;
        },
                searchField.textProperty(),
                teamFilter.valueProperty(),
                positionFilter.valueProperty()
                ));
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

        teamFilter.getItems().addAll(teams);
    }
}
