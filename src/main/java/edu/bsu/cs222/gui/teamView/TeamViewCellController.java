package edu.bsu.cs222.gui.teamView;

import edu.bsu.cs222.gui.playersView.PlayersViewCellController;
import edu.bsu.cs222.gui.playersView.playerStats.PlayerStatsModalController;
import edu.bsu.cs222.model.Player;
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

import java.io.IOException;
import java.util.Objects;

public class TeamViewCellController {
    @FXML private Label lastMatchLbl;
    @FXML private Label lastWeekLbl;
    @FXML private Label seasonLbl;
    private TeamViewController parent;
    private Player currentPlayer;
    @FXML private ImageView headshot;
    @FXML private Label nameLbl;
    @FXML private Label detailsLbl;
    @FXML private Label statsLbl;
    private String lastUrl;

    private static final Image DEFAULT = new Image (Objects.requireNonNull(PlayersViewCellController.class.getResource("/images/default_avatar.jpg")).toExternalForm(), 70, 70, true, true);

    @FXML
    public void initialize(){
        headshot.setFitWidth(70);
        headshot.setFitHeight(70);
        headshot.setPreserveRatio(true);
        headshot.setSmooth(true);
    }

    public void setData(Player player, boolean networkError) throws InterruptedException, IOException {
        currentPlayer = player;
        String playerTeam = (player.getTeam() == null ? "NA" : player.getTeam());
        String playerNumber = (player.getJerseyNumber() == null ? "NA" : player.getJerseyNumber());
        String playerExp = (player.getExperience() == null ? "NA" : player.getExperience());
        String playerHeight = (player.getHeight() == null ? "NA" : player.getHeight());
        String playerWeight = (player.getWeight() == null ? "NA" : player.getWeight());
        String playerSchool = (player.getSchool() == null ? "NA" : player.getSchool());

        StringBuilder lastWeekSoreBuilder = new StringBuilder("Last Week: ");
        StringBuilder seasonScoreBuilder = new StringBuilder("Season: ");

        if (networkError){
            lastWeekSoreBuilder.append("0pts");
            seasonScoreBuilder.append("0pts");
            lastMatchLbl.setText("NA");
        } else{
            lastWeekSoreBuilder.append(player.getWeekScore(parent.getCurrentTeam().getCoefficientMap())).append("pts");
            seasonScoreBuilder.append(player.getSeasonScore(parent.getCurrentTeam().getCoefficientMap())).append("pts");
            lastMatchLbl.setText(player.getLastGame());
        }

        nameLbl.setText(String.format("%s #%s", player.getName(), playerNumber));
        detailsLbl.setText(String.format("%s | %s | %s", playerTeam, parent.getCurrentTeam().getPlayerMap().get(player).toString(), playerSchool));
        statsLbl.setText(String.format("Exp: %syr | %s %slbs", playerExp, playerHeight, playerWeight));
        lastWeekLbl.setText(lastWeekSoreBuilder.toString());
        seasonLbl.setText(seasonScoreBuilder.toString());

        String imageUrl = (player.getHeadshot() == null ? "" : player.getHeadshot());

        if (imageUrl.equals(lastUrl)) {return;}

        lastUrl = imageUrl;

        headshot.setImage(DEFAULT);

        if (imageUrl.equals("not found") ||imageUrl.isBlank()){return;}

        Image headshotImage = new Image(imageUrl, 70, 70, true, true, true);

        headshotImage.progressProperty().addListener((_, _, nv) -> {
            if (nv.doubleValue() >= 1.0 && !headshotImage.isError() && imageUrl.equals(lastUrl)){
                headshot.setImage(headshotImage);
            }
        });

        headshotImage.errorProperty().addListener((_, _, isErr) -> {
            if (isErr && imageUrl.equals(lastUrl)) {
                headshot.setImage(DEFAULT);
            }
        });

        if (!headshotImage.isBackgroundLoading() && imageUrl.equals(lastUrl)){
            headshot.setImage(headshotImage);
        }
    }

    public void removePlayer() {
        parent.getCurrentTeam().removePlayer(currentPlayer);
        parent.removePlayerFromList(currentPlayer);
    }

    public void setParentController(TeamViewController parent) {
        this.parent = parent;
    }

    public void viewPlayerStats() throws IOException, InterruptedException {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("View Player Stats");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/playerStats/PlayerStatsModal.fxml")));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        PlayerStatsModalController controller = loader.getController();
        controller.setPlayer(currentPlayer);

        Button cancelButton = (Button) root.lookup("#cancelButton");

        cancelButton.setOnAction(_ -> creator.close());

        creator.setOnCloseRequest(_ -> creator.close());

        creator.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE){
                creator.close();
            }
        });

        creator.showAndWait();
    }
}
