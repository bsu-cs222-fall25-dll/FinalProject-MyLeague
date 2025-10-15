package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.PlayerCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.ArrayList;

public class PlayersViewController {
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    private FilteredList<Player> filteredList;

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayerCell());
    }

    public void setPlayers(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);

        searchField.textProperty().addListener((obsV, oldValue, newValue) -> {
            String query = (newValue == null) ? "" : newValue.trim().toLowerCase();
            filteredList.setPredicate(p -> query.isEmpty() ||
                    (p.getName() != null && p.getName().toLowerCase().contains(query)));
        });
    }
}
