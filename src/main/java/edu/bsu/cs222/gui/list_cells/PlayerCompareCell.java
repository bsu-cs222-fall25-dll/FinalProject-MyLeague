package edu.bsu.cs222.gui.list_cells;

import edu.bsu.cs222.gui.controllers.PlayerCompareCellController;
import edu.bsu.cs222.model.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.Objects;

public class PlayerCompareCell extends ListCell<Player> {
    private FXMLLoader loader;
    private PlayerCompareCellController controller;

    @Override
    protected void updateItem(Player player, boolean empty){
        super.updateItem(player, empty);

        if (empty || player == null){
            setText(null);
            return;
        }

        if (loader == null){
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/PlayerCompareCell.fxml")));

            try {
                loader.load();
                controller = loader.getController();
            } catch (IOException e) {
                setText("Failed to load cell");
                return;
            }
        }

        controller.setData(player);
    }
}
