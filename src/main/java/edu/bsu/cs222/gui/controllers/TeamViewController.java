package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.GraphicalUserInterface;
import javafx.event.ActionEvent;

import java.io.IOException;

public class TeamViewController {
    public void openPlayersView(ActionEvent actionEvent) throws IOException {
        GraphicalUserInterface.setRoot("/PlayersView.fxml");
    }
}
