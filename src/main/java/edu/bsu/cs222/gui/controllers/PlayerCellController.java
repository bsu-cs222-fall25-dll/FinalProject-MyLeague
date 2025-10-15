package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class PlayerCellController {
    @FXML private ImageView headshot;
    @FXML private Label nameLbl;
    @FXML private Label statsLbl;

    private static final Image DEFAULT = new Image (Objects.requireNonNull(PlayerCellController.class.getResource("/default_avatar.jpg")).toExternalForm(), 70, 70, true, true);

    private String lastUrl;

    @FXML
    public void initialize(){
        headshot.setFitWidth(70);
        headshot.setFitHeight(70);
        headshot.setPreserveRatio(true);
        headshot.setSmooth(true);
    }

    public void setData(Player player){
        nameLbl.setText(player.getName() == null ? "" : player.getName());
        statsLbl.setText(player.getPosition() == null ? "" : player.getPosition());

        String imageUrl = (player.getHeadshot() == null ? "" : player.getHeadshot());

        if (imageUrl.equals(lastUrl)) {return;}

        lastUrl = imageUrl;

        headshot.setImage(DEFAULT);

        if (imageUrl.equals("not found") ||imageUrl.isBlank()){return;}

        Image headshotImage = new Image(imageUrl, 70, 70, true, true, true);

        headshotImage.progressProperty().addListener((obs, ov, nv) -> {
            if (nv.doubleValue() >= 1.0 && !headshotImage.isError() && imageUrl.equals(lastUrl)){
                headshot.setImage(headshotImage);
            }
        });

        headshotImage.errorProperty().addListener((obs, wasErr, isErr) -> {
            if (isErr && imageUrl.equals(lastUrl)) {
                headshot.setImage(DEFAULT);
            }
        });

        if (!headshotImage.isBackgroundLoading() && imageUrl.equals(lastUrl)){
            headshot.setImage(headshotImage);
        }
    }
}
