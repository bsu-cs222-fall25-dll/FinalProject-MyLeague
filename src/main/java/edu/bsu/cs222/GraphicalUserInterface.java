package edu.bsu.cs222;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

import static javafx.application.Application.launch;

public class GraphicalUserInterface {
    public static void main(String[] args) {
        launch(args);
    }

    private final Button retrieveButton = new Button("Retrieve");
    private final TextField inputField = new TextField();
    private final TextArea outputArea = new TextArea();

    public void start(Stage primaryStage) {
        configure(primaryStage);
    }

    private void configure(Stage stage) {
        stage.setTitle("MyLeague");

        stage.setScene(new Scene(createRoot()));
        stage.setWidth(500);
        stage.setMinHeight(outputArea.getHeight());
        outputArea.setMinHeight(300);
        stage.show();
    }

    private Pane createRoot() {
        VBox root = new VBox();
        root.getChildren().addAll( //
                new Label("Input a Wikipedia Article Name: "), //
                inputField, //
                retrieveButton, //
                new Label("Redirects History found:"),//
                outputArea);
        return root;
    }

//    private void configureRetrieveButton(){
//        retrieveButton.setOnAction(event -> retrieveRevisions());
//    }
}

