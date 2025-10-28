package edu.bsu.cs222.gui;

import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.PlayerRetriever;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.bsu.cs222.model.Position.*;

public class GraphicalUserInterface extends Application {
    private static Scene scene;
    private static final ArrayList<League> leagueList = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        boolean networkError = PlayerRetriever.getPlayersFromJsonOrApi();
        if (networkError){
            ErrorModal.throwErrorModal("Network Error", null);
        }
        else {
            leagueList.add(new League("Default", new ArrayList<>(List.of(QB, WR, WR, RB, RB, TE, FLEX, K))));
            FXMLLoader playersViewLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML_Files/PlayersView.fxml")));
            scene = new Scene(playersViewLoader.load(), 600, 400);

            stage.setTitle("MyLeague");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event -> stage.close());
        }
    }

    public static void setRoot(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(GraphicalUserInterface.class.getResource(fxmlFile)));
        scene.setRoot(root);
    }

    public static ArrayList<League> getLeagueList(){
        return leagueList;
    }

    public static void addLeague(League league){
        leagueList.add(league);
    }
}
