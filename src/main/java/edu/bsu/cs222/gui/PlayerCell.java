package edu.bsu.cs222.gui;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.controllers.PlayerCellController;
import edu.bsu.cs222.gui.controllers.PlayersViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.Objects;

public class PlayerCell extends ListCell<Player> {
    private PlayerCellController controller;
    private Node view;
    private FXMLLoader loader;
    private PlayersViewController parent;

    public PlayerCell() {
    }

    @Override
    protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (loader == null){
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/PlayerCell.fxml")));

            try {
                view = loader.load();
                controller = loader.getController();
                controller.setParentController(parent);
            } catch (IOException e) {
                setText("Failed to load cell");
                setGraphic(null);
                return;
            }
        }

        controller.setData(player);
        setGraphic(view);
    }

    public void setParentController(PlayersViewController parent) {
        this.parent = parent;
    }
}
