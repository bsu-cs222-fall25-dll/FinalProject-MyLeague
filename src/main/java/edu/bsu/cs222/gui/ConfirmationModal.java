package edu.bsu.cs222.gui;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ConfirmationModal {
    public static boolean throwConfirmationModal(String confirmationMessage) {
        boolean[] confirmed = {true};
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent root = null;

        stage.setTitle("Confirm");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ConfirmationModal.class.getResource("/fxml_files/ConfirmationModal.fxml")));
        try {
            root = loader.load();
        } catch (IOException _) {
            System.err.println("ConfirmationModal.fxml not found");
            System.exit(1);
        }
        stage.setScene(new Scene(root));

        Label messageLbl = (Label) root.lookup("#messageLbl");
        messageLbl.setText(confirmationMessage);

        stage.setOnCloseRequest(Event::consume);

        Button noButton = (Button) root.lookup("#noButton");
        Button yesButton = (Button) root.lookup("#yesButton");

        noButton.setOnAction(_ -> {
            confirmed[0] = false;
            stage.close();
        });
        
        yesButton.setOnAction(_ -> stage.close());

        stage.showAndWait();
        return confirmed[0];
    }
}
