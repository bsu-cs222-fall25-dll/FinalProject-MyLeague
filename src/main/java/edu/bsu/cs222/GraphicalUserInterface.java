package edu.bsu.cs222;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.bsu.cs222.gui.controllers.Position.*;

public class GraphicalUserInterface extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Scene scene;
    private static final ArrayList<League> leagueList = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        leagueList.add(new League("Default", new ArrayList<>(List.of(QB, WR, WR, RB, RB, TE, FLEX, K))));
        FXMLLoader playersViewLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/PlayersView.fxml")));
        scene = new Scene(playersViewLoader.load(), 600, 400);

        stage.setTitle("MyLeague");
        stage.setScene(scene);
        stage.show();
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
