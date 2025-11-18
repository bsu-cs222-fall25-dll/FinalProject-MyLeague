package edu.bsu.cs222.gui.playersView.playerStats;

import edu.bsu.cs222.model.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.Objects;

public class PlayerCompareCell extends ListCell<Player> {
    private final PlayerStatsModalController parent;
    private FXMLLoader loader;
    private PlayerCompareCellController controller;
    private Node view;

    public PlayerCompareCell(PlayerStatsModalController parent){this.parent = parent;}

    @Override
    protected void updateItem(Player player, boolean empty){
        super.updateItem(player, empty);

        if (empty || player == null){
            setText(null);
            setGraphic(null);
            return;
        }

        if (loader == null){
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/playerStats/PlayerCompareCell.fxml")));

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
