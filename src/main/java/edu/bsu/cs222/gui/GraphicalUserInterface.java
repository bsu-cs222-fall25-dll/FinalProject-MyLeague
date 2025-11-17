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
    public void start(Stage stage) throws IOException, InterruptedException {
        boolean networkError = PlayerRetriever.getPlayersFromJsonOrApi();
        if (networkError){
            ErrorModal.throwErrorModal("Network Error", null);
        }
        else {
            HashMap<String, Double> defaultCoefficientMap = getDefaultCoefficientMap();


            leagueList.add(new League("Default", new ArrayList<>(List.of(QB, WR, WR, RB, RB, TE, FLEX, K)), defaultCoefficientMap));
            FXMLLoader playersViewLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/PlayersView.fxml")));
            scene = new Scene(playersViewLoader.load(), 700, 500);

            stage.setTitle("MyLeague");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event -> stage.close());
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
