package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.GraphicalUserInterface;

import java.io.IOException;

public class TeamViewController {
    public void openPlayersView() throws IOException {
        GraphicalUserInterface.setRoot("/PlayersView.fxml");
    }
}
