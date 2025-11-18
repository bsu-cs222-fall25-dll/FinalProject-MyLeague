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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static edu.bsu.cs222.model.Position.*;

public class GraphicalUserInterface extends Application {
    private static Scene scene;
    private static final ArrayList<League> leagueList = new ArrayList<>();
    
    @Override
    public void start(Stage stage) {
        try {
            PlayerRetriever.getPlayersFromJsonOrApi();

            HashMap<String, Double> defaultCoefficientMap = getDefaultCoefficientMap();


            leagueList.add(new League("Default", new ArrayList<>(List.of(QB, WR, WR, RB, RB, TE, FLEX, K)), defaultCoefficientMap));
            FXMLLoader playersViewLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/PlayersView.fxml")));
            try {
                scene = new Scene(playersViewLoader.load(), 700, 500);
            } catch (IOException _) {
                System.err.println("PlayersView.fxml not found");
                System.exit(1);
            }

            stage.setTitle("MyLeague");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(_ -> stage.close());
        }
        catch (Exception _){
            ErrorModal.throwErrorModal("Network Error", null);
        }
    }

    private HashMap<String, Double> getDefaultCoefficientMap() {
        HashMap<String, Double> defaultCoefficientMap = new HashMap<>();
        defaultCoefficientMap.put("rushYards", .1);
        defaultCoefficientMap.put("recYards", .1);
        defaultCoefficientMap.put("passYards", .04);
        defaultCoefficientMap.put("rushTds", 7.0);
        defaultCoefficientMap.put("recTds", 7.0);
        defaultCoefficientMap.put("passTds", 4.0);
        defaultCoefficientMap.put("receptions", 1.0);
        defaultCoefficientMap.put("interceptions", -2.0);
        defaultCoefficientMap.put("fumbles", -2.0);
        defaultCoefficientMap.put("xpMade", 2.0);
        defaultCoefficientMap.put("fgMade", 4.0);
        return defaultCoefficientMap;
    }

    public static void setRoot(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(GraphicalUserInterface.class.getResource(fxmlFile)));
            scene.setRoot(root);
        }
        catch (IOException _){
            System.err.printf("%s not found", fxmlFile);
            System.exit(1);
        }
    }

    public static ArrayList<League> getLeagueList(){
        return leagueList;
    }

    public static void addLeague(League league){
        leagueList.add(league);
    }
}
