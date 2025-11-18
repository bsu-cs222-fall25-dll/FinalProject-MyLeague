package edu.bsu.cs222.gui.teamView;

import edu.bsu.cs222.model.Player;
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
            } catch (IOException _) {
                System.err.println("TeamViewCell.fxml not found");
                System.exit(1);
            }
        }

        controller.setData(player, networkError);
        setGraphic(view);
    }
}
