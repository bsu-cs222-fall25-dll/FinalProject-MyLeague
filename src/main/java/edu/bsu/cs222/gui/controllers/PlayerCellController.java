package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.League;
import edu.bsu.cs222.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static edu.bsu.cs222.gui.controllers.Position.*;

public class PlayerCellController {
    @FXML private ImageView headshot;
    @FXML private Label nameLbl;
    @FXML private Label detailsLbl;
    @FXML private Label statsLbl;
    private PlayersViewController parent;
    private Player currentPlayer;

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
        currentPlayer = player;
        String playerTeam = (player.getTeam() == null ? "NA" : player.getTeam());
        String playerPosition = (player.getPosition() == null ? "NA" : player.getPosition().toString());
        String playerNumber = (player.getJerseyNumber() == null ? "NA" : player.getJerseyNumber());
        String playerExp = (player.getExperience() == null ? "NA" : player.getExperience());
        String playerHeight = (player.getHeight() == null ? "NA" : player.getHeight());
        String playerWeight = (player.getWeight() == null ? "NA" : player.getWeight());
        String playerSchool = (player.getSchool() == null ? "NA" : player.getSchool());

        nameLbl.setText(String.format("%s #%s", player.getName(), playerNumber));
        detailsLbl.setText(String.format("%s | %s | %s", playerTeam, playerPosition, playerSchool));
        statsLbl.setText(String.format("Exp: %syr | %s %slbs", playerExp, playerHeight, playerWeight));

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

    public void playerAdder() throws IOException {
        parent.setDisable(true);
        League.Team team = parent.getCurrentTeam();
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("Player Adder");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/PlayerAdderModal.fxml")));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button qbButton = (Button) root.lookup("#qbButton");
        Button rbButton = (Button) root.lookup("#rbButton");
        Button wrButton = (Button) root.lookup("#wrButton");
        Button teButton = (Button) root.lookup("#teButton");
        Button kButton = (Button) root.lookup("#kButton");
        Button flexButton = (Button) root.lookup("#flexButton");

        for (Position position: team.getFreePositions()){
            if (position == QB){ qbButton.setDisable(false);}
            if (position == RB){ rbButton.setDisable(false);}
            if (position == WR){ wrButton.setDisable(false);}
            if (position == TE){ teButton.setDisable(false);}
            if (position == K){ kButton.setDisable(false);}
            if (position == FLEX){ flexButton.setDisable(false);}
        }

        cancelButton.setOnAction(e ->{
            parent.setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(event ->{
            parent.setDisable(false);
            creator.close();
        });

        qbButton.setOnAction(e -> addPlayer(QB, creator));
        rbButton.setOnAction(e -> addPlayer(RB, creator));
        wrButton.setOnAction(e -> addPlayer(WR, creator));
        teButton.setOnAction(e -> addPlayer(TE, creator));
        kButton.setOnAction(e -> addPlayer(K, creator));
        flexButton.setOnAction(e -> addPlayer(FLEX, creator));

        creator.showAndWait();
    }

    public void setParentController(PlayersViewController parent) {
        this.parent = parent;
    }

    private void addPlayer(Position position, Stage stage){
        parent.getCurrentTeam().addPlayer(currentPlayer, position);
        parent.setDisable(false);
        HashMap<Player, Position> map = parent.getCurrentTeam().getPlayerMap();
        for (Player key: map.keySet()){
            System.out.println(key.getName() +" " + map.get(key));
        }
        System.out.println();
        for (Position position0: parent.getCurrentTeam().getFreePositions()){
            System.out.println(position0);
        }
        System.out.println();
        stage.close();
    }
}
