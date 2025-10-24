package edu.bsu.cs222.gui;

import edu.bsu.cs222.Player;
import edu.bsu.cs222.gui.controllers.TeamViewCellController;
import edu.bsu.cs222.gui.controllers.TeamViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.Objects;

public class TeamViewCell extends ListCell<Player> {
    private TeamViewCellController controller;
    private final TeamViewController parent;
    private Node view;
    private FXMLLoader loader;

    public TeamViewCell(TeamViewController parent) {this.parent = parent;}

    @Override
    protected void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);

        if (empty || player == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (loader == null){
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML_Files/TeamViewCell.fxml")));

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
