package edu.bsu.cs222.gui;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.controllers.PlayerCellController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class PlayerCell extends ListCell<Player> {
    private PlayerCellController controller;
    private boolean loaded = false;
    private Node view;


    private void ensureLoaded(){
        if(loaded) {return;}
        try {
            URL fxml = getClass().getResource("/PlayerCell.fxml");
            if (fxml == null){
                throw new IllegalStateException("No '/PlayerCell.fxml' found");
            }
            FXMLLoader loader = new FXMLLoader(fxml);
            view = loader.load();
            controller = loader.getController();
            if (controller == null){
                throw new IllegalStateException("Controller is null");
            }
            loaded = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load /PLayerCell.fxml", e);
        }
    }

    public PlayerCell() {
        loaded = false;
    }

    @Override
    protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
            setText(null);
            setGraphic(null);
        } else {
            ensureLoaded();
            controller.setData(player.getName(), player.getPosition(), player.getHeadshot());
            setGraphic(view);
        }
    }
}
