package edu.bsu.cs222.gui.list_cells;

import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.gui.controllers.PlayersViewCellController;
import edu.bsu.cs222.gui.controllers.PlayersViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.Objects;

public class PlayersViewCell extends ListCell<Player> {
    private PlayersViewCellController controller;
    private Node view;
    private FXMLLoader loader;
    private final PlayersViewController parent;

    public PlayersViewCell(PlayersViewController parent) {this.parent = parent;}

    @Override
    protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (loader == null){
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/PlayersViewCell.fxml")));

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
}
