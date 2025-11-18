package edu.bsu.cs222.gui.list_cells;

import edu.bsu.cs222.model.Player;
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
    private final boolean networkError;

    public TeamViewCell(TeamViewController parent, boolean networkError) {
        this.parent = parent;
        this.networkError = networkError;
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
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/teamView/TeamViewCell.fxml")));

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

        try {
            controller.setData(player, networkError);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        setGraphic(view);
    }
}
