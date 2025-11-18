package edu.bsu.cs222.gui;

import edu.bsu.cs222.gui.playersView.PlayersViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ErrorModal {
    public static void throwErrorModal(String errorMessage, Object parent) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        boolean playersView = parent != null && parent.getClass() == PlayersViewController.class;
        Parent root = null;

        stage.setTitle("Error");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ErrorModal.class.getResource("/fxml_files/ErrorModal.fxml")));
        try {
            root = loader.load();
        } catch (IOException _) {
            System.err.println("ErrorModal.fxml not found");
            System.exit(1);
        }
        stage.setScene(new Scene(root));

        Label errorLbl = (Label) root.lookup("#errorLbl");
        errorLbl.setText(errorMessage);

        stage.setOnCloseRequest(_ ->{
            stage.close();
            if (playersView) {((PlayersViewController) parent).setDisable(false);}
        });

        Button closeButton = (Button) root.lookup("#closeButton");

        closeButton.setOnAction(_ -> {
            stage.close();
            if (playersView) {
                ((PlayersViewController) parent).setDisable(false);
            }
        });

        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE){
                stage.close();
                if (playersView) {
                    ((PlayersViewController) parent).setDisable(false);
                }
            }
        });

        stage.showAndWait();
    }
}
