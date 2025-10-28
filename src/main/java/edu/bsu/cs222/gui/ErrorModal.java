package edu.bsu.cs222.gui;

import edu.bsu.cs222.gui.controllers.PlayersViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ErrorModal {
    public static void throwErrorModal(Stage stage, String errorMessage, Object parent) throws IOException {
        boolean disable = parent.getClass() == PlayersViewController.class;

        stage.setTitle("Error");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ErrorModal.class.getResource("/FXML_Files/ErrorModal.fxml")));
        Parent root = loader.load();
        stage.setScene(new Scene(root));

        Label errorLbl = (Label) root.lookup("#errorLbl");
        errorLbl.setText(errorMessage);

        stage.setOnCloseRequest(event ->{
            stage.close();
            if (disable) {((PlayersViewController) parent).setDisable(false);}
        });

        Button closeButton = (Button) root.lookup("#closeButton");

        closeButton.setOnAction(e -> {
            stage.close();
            if (disable) {
                ((PlayersViewController) parent).setDisable(false);
            }
        });

        stage.showAndWait();
    }
}
