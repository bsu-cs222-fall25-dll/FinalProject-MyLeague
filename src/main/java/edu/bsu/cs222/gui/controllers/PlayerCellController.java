package edu.bsu.cs222.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class PlayerCellController {
    @FXML private ImageView avatar;
    @FXML private Label nameLbl;
    @FXML private Label statsLbl;

    private static final Image DEFAULT = new Image (PlayerCellController.class.getResource("/default_avatar.jpg").toExternalForm(), true);

    @FXML
    public void initialize(){}

    public void setData(String name, String stats, String imageURL){
        nameLbl.setText(name == null ? "" : name);
        statsLbl.setText(stats == null ? "" : stats);

        if (imageURL.equals("not found")){
            avatar.setImage(DEFAULT);
            return;
        }

        Image image = new Image(imageURL, true);
        avatar.setImage(image);

        image.errorProperty().addListener((obs, wasErr, isErr) -> {
            if (isErr) avatar.setImage(DEFAULT);
        });
    }
}
