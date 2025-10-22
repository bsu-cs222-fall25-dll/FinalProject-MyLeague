package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.GraphicalUserInterface;
import edu.bsu.cs222.League;
import edu.bsu.cs222.Player;
import edu.bsu.cs222.PlayerRetriever;
import edu.bsu.cs222.gui.PlayerCell;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static edu.bsu.cs222.gui.controllers.Position.*;

public class PlayersViewController {
    @FXML private Button teamViewButton;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;

    private League currentLeague = GraphicalUserInterface.getLeagueList().getFirst();
    private String leagueString = "Default";
    private String previousLeagueString = "Default";
    private boolean ignoreSelection = false;

    @FXML
    public void initialize() throws IOException, InterruptedException {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayerCell());

        positionFilter.setValue("All");
        teamFilter.setValue("All");
        leagueSelector.setValue("Default");
        teamSelector.setValue("No Teams");

        PlayerRetriever retriever = new PlayerRetriever();
        retriever.getPlayersFromJsonOrApi();
        managePlayersView(retriever.getPlayerArrayList());
    }

    public void managePlayersView(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);
        setPositionsAndTeams(players);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player player) -> {
            String searchText = searchField.getText();
            ArrayList<String> queries = (searchText == null || searchText.isBlank()) ? new ArrayList<>(): new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));
            if (!runSearch(queries, player)) {return false;}

            String positionString = positionFilter.getValue();
            if (!positionString.isBlank()){
                if(!positionString.equals("All")){
                    if (player.getPosition() != Position.valueOf(positionString)) {return false;}
                }
            }

            String team = teamFilter.getValue();
            if (!team.isBlank()){
                if(!team.equals("All")){
                    if (team.equals("None")){
                        if (!player.getTeam().isBlank()) {return false;}
                    }
                    if (!team.equals(player.getTeam())) {return false;}
                }
            }
            return true;
        },
                searchField.textProperty(),
                teamFilter.valueProperty(),
                positionFilter.valueProperty()
                ));

        setLeagueItems();

        teamSelector.getItems().add("Create");

        leagueSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (ignoreSelection) {return;}

            leagueString = leagueSelector.getValue();
            if (!Objects.equals(leagueString, "Create")){
                previousLeagueString = leagueString;
            }

            if (Objects.equals(leagueString, "Create")) {
                try {
                    ignoreSelection = true;
                    teamSelector.getSelectionModel().clearSelection();
                    setDisable(true);
                    leagueCreator();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                for (League league : GraphicalUserInterface.getLeagueList()) {
                    if (league.toString().equals(leagueString)) {
                        if (currentLeague != league) {
                            currentLeague = league;
                            teamSelector.getItems().removeAll();
                        }
                    }
                }
            }

            if (currentLeague != null) {
                if(currentLeague.getTeamNames().isEmpty()){
                    teamSelector.getItems().add("None");
                    teamSelector.getItems().add("Create");
                }
                else {
                    teamSelector.getItems().addAll(currentLeague.getTeamNames());
                    teamSelector.getItems().add("Create");
                }
            }
        });

        teamSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            String teamName = teamSelector.getValue();

            if (teamName.equals("Create")) {
                leagueSelector.setValue(" ");
                setDisable(true);
            }
        });
    }

    private boolean runSearch(ArrayList<String> queries, Player player){
        if (player == null) {return false;}
        boolean match = true;
        for (String query : queries){
            if (!player.getName().toLowerCase().contains(query)) {
                match = false;
                break;
            }
        }
        return match;
    }

    public void setPositionsAndTeams(ArrayList<Player> players) {
        Set<String> teams = new TreeSet<>();
        for (Player player : players){
            String team = player.getTeam();
            teams.add(team);
        }

        positionFilter.getItems().add("All");
        teamFilter.getItems().add("All");

        for (Position position: Position.values()){
            if(position != FLEX){
                positionFilter.getItems().add(position.toString());
            }
        }

        teamFilter.getItems().addAll(teams);
    }

    public void openTeamView(ActionEvent actionEvent) throws IOException {
        GraphicalUserInterface.setRoot("/TeamView.fxml");
    }

    private void setDisable(boolean disable){
        leagueSelector.setDisable(disable);
        teamSelector.setDisable(disable);
        positionFilter.setDisable(disable);
        teamFilter.setDisable(disable);
        searchField.setDisable(disable);
        teamViewButton.setDisable(disable);
    }

    private void leagueCreator() throws IOException {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("League Creator");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/LeagueCreatorModal.fxml")));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");
        TextField nameField = (TextField) root.lookup("#nameField");

        createButton.setOnAction(e ->{
            if (!nameField.getText().isBlank()){
                League league = new League(nameField.getText(), new ArrayList<>(List.of(QB, RB, TE, K, FLEX)));
                GraphicalUserInterface.addLeague(league);
                setLeagueItems();
                leagueSelector.setValue(league.getName());
                teamSelector.setValue("None");
                setDisable(false);
                creator.close();
                ignoreSelection = false;
            }
        });

        cancelButton.setOnAction(e ->{
            leagueSelector.setValue(previousLeagueString);
            teamSelector.setValue("None");
            setDisable(false);
            creator.close();
        });

        creator.show();
    }

    private void setLeagueItems(){
        ArrayList<String> leagueItemList = new ArrayList<>();
        for (League league: GraphicalUserInterface.getLeagueList()){
            leagueItemList.add(league.getName());
        }
        leagueItemList.add("Create");
        leagueSelector.setItems(FXCollections.observableList(leagueItemList));
    }
}
