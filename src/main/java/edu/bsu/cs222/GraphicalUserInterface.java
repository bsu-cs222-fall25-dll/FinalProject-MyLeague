package edu.bsu.cs222;

import edu.bsu.cs222.gui.controllers.PlayersViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class GraphicalUserInterface extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/PlayersView.fxml")));
        Scene scene = new Scene(loader.load(), 600, 400);

        PlayersViewController controller = loader.getController();
        PlayerRetriever retriever = new PlayerRetriever();

        retriever.getPlayersFromJsonOrApi();

        controller.setPlayers(retriever.getPlayerArrayList());

        stage.setTitle("MyLeague");
        stage.setScene(scene);
        stage.show();
    }
}
