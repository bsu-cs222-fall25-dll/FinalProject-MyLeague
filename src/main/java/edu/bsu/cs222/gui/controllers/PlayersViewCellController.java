package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.Position;
import edu.bsu.cs222.gui.ErrorModal;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
import java.util.Objects;
import static edu.bsu.cs222.model.Position.*;

public class PlayersViewCellController {
    @FXML private Button addPlayerButton;
    @FXML private ImageView headshot;
    @FXML private Label nameLbl;
    @FXML private Label detailsLbl;
    @FXML private Label statsLbl;
    private PlayersViewController parent;
    private Player currentPlayer;

    private static final Image DEFAULT = new Image (Objects.requireNonNull(PlayersViewCellController.class.getResource("/images/default_avatar.jpg")).toExternalForm(), 70, 70, true, true);

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

        if (imageUrl.equals("Not Found") ||imageUrl.isBlank()){return;}

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

        League.Team currentTeam = parent.getCurrentTeam();

        if (currentTeam != null ) {
            addPlayerButton.setDisable(currentTeam.getPlayerNameList().contains(player.getName()));
        }
    }

    public void playerAdder() throws IOException {
        parent.setDisable(true);
        League.Team team = parent.getCurrentTeam();
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);

        if (team == null){
            ErrorModal.throwErrorModal("Please select a team before attempting to add a player", parent);
        }
        else {
            creator.setTitle("Player Adder");

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/PlayerAdderModal.fxml")));
            Parent root = loader.load();

            creator.setScene(new Scene(root));

            Button qbButton = (Button) root.lookup("#qbButton");
            Button rbButton = (Button) root.lookup("#rbButton");
            Button wrButton = (Button) root.lookup("#wrButton");
            Button teButton = (Button) root.lookup("#teButton");
            Button kButton = (Button) root.lookup("#kButton");
            Button flexButton = (Button) root.lookup("#flexButton");

            for (Position position: team.getFreePositions()) {
                if (position == QB && currentPlayer.getPosition() == QB) {
                    qbButton.setDisable(false);
                }
                if (position == RB && currentPlayer.getPosition() == RB) {
                    rbButton.setDisable(false);
                }
                if (position == WR && currentPlayer.getPosition() == WR) {
                    wrButton.setDisable(false);
                }
                if (position == TE && currentPlayer.getPosition() == TE) {
                    teButton.setDisable(false);
                }
                if (position == K && currentPlayer.getPosition() == K) {
                    kButton.setDisable(false);
                }
                if (position == FLEX && currentPlayer.getPosition() == RB
                        || position == FLEX && currentPlayer.getPosition() == WR
                        || position == FLEX && currentPlayer.getPosition() == TE){
                    flexButton.setDisable(false);
                }
            }

            qbButton.setOnAction(e -> addPlayer(QB, creator));
            rbButton.setOnAction(e -> addPlayer(RB, creator));
            wrButton.setOnAction(e -> addPlayer(WR, creator));
            teButton.setOnAction(e -> addPlayer(TE, creator));
            kButton.setOnAction(e -> addPlayer(K, creator));
            flexButton.setOnAction(e -> addPlayer(FLEX, creator));

            creator.setOnCloseRequest(event ->{
                parent.setDisable(false);
                creator.close();
            });

            Button closeButton = (Button) root.lookup("#cancelButton");

            closeButton.setOnAction(e -> {
                parent.setDisable(false);
                creator.close();
            });

            creator.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE){
                    parent.setDisable(false);
                    creator.close();
                }
            });

            creator.showAndWait();
        }
    }

    public void setParentController(PlayersViewController parent) {
        this.parent = parent;

        ChangeListener<League.Team> teamListener = (obs, oldVal, newVal) -> updateAddButton(newVal);
        parent.currentTeamProperty().addListener(teamListener);
    }

    private void addPlayer(Position position, Stage stage){
        parent.getCurrentTeam().addPlayer(currentPlayer, position);
        parent.setDisable(false);
        stage.close();
        addPlayerButton.setDisable(true);
    }

    private void updateAddButton(League.Team team){
        if (team == null) {
            addPlayerButton.setDisable(true);
            return;
        }
        addPlayerButton.setDisable(team.getPlayerNameList().contains(currentPlayer.getName()));
    }

    public void viewPlayerStats() throws IOException, InterruptedException {
            parent.setDisable(true);
            Stage creator = new Stage();
            creator.initModality(Modality.APPLICATION_MODAL);
            creator.setTitle("View Player Stats");

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/PlayerStatsModal.fxml")));
            Parent root = loader.load();

            creator.setScene(new Scene(root));

            PlayerStatsModalController controller = loader.getController();
            controller.setPlayer(currentPlayer);

            Button cancelButton = (Button) root.lookup("#cancelButton");

            cancelButton.setOnAction(e -> {
                parent.setDisable(false);
                creator.close();
            });

            creator.setOnCloseRequest(event -> {
                parent.setDisable(false);
                creator.close();
            });

            creator.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE){
                    parent.setDisable(false);
                    creator.close();
                }
            });

            creator.showAndWait();
    }
}
