package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.gui.GraphicalUserInterface;
import edu.bsu.cs222.gui.PlayersViewCell;
import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.PlayerRetriever;
import edu.bsu.cs222.model.Position;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static edu.bsu.cs222.model.Position.*;

public class PlayersViewController {
    @FXML private Button teamViewButton;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;

    private final ReadOnlyObjectWrapper<League.Team> currentTeam = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<League.Team> currentTeamProperty() {
        return currentTeam.getReadOnlyProperty();
    }

    public League.Team getCurrentTeam() {
        if (teamSelector.getValue().equals("None")){
            return null;
        }
        return Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(teamSelector.getValue());
    }

    private String previousLeagueString = "Default";
    private String previousTeamString = "None";

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lv -> new PlayersViewCell(this));
        positionFilter.setValue("All");
        teamFilter.setValue("All");
        leagueSelector.setValue("Default");

        managePlayersView(PlayerRetriever.getPlayerArrayList());
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
                    return team.equals(player.getTeam());
                }
            }
            return true;
        },
                searchField.textProperty(),
                teamFilter.valueProperty(),
                positionFilter.valueProperty()
                ));

        setLeagueItems();

        setTeamItems(GraphicalUserInterface.getLeagueList().getFirst());
        teamSelector.setValue(teamSelector.getItems().getFirst());

        leagueSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (Objects.equals(newVal, "Create")) {
                try {
                    teamSelector.getSelectionModel().clearSelection();
                    setDisable(true);
                    leagueCreator();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (!Objects.equals(oldVal, "Create")) {previousLeagueString = oldVal;}
                setTeamItems(Objects.requireNonNull(getLeagueByName(newVal)));
                if (Objects.requireNonNull(getLeagueByName(newVal)).getTeamNames().isEmpty()){
                    teamSelector.setValue("None");
                }
                else {
                    teamSelector.setValue(teamSelector.getItems().getFirst());
                }
            }
        });

        teamSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!Objects.equals("Create", oldVal) && !oldVal.isBlank()) {
                previousTeamString = oldVal;
            }
            if (newVal != null && !newVal.equals("None")) {
                if (Objects.equals(newVal, "Create")) {
                    try {
                        setDisable(true);
                        teamCreator(getLeagueByName(leagueSelector.getValue()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    currentTeam.set(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(newVal));
                }
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

    public void openTeamView() throws IOException {
        GraphicalUserInterface.setRoot("/FXML_Files/TeamView.fxml");
    }

    public void setDisable(boolean disable){
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_Files/LeagueCreatorModal.fxml"));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");
        TextField nameField = (TextField) root.lookup("#nameField");

        createButton.setOnAction(e -> createLeague(nameField.getText(), creator));

        createButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                createLeague(nameField.getText(), creator);
                event.consume();
            }
        });

        cancelButton.setOnAction(e ->{
            leagueSelector.setValue(previousLeagueString);
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(event ->{
            leagueSelector.setValue(previousLeagueString);
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.showAndWait();
    }

    private void teamCreator(League league) throws IOException {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("Team Creator");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/FXML_Files/TeamCreatorModal.fxml")));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");
        TextField nameField = (TextField) root.lookup("#nameField");

        createButton.setOnAction(e -> createTeam(nameField.getText(), creator, league));

        creator.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                createTeam(nameField.getText(), creator, league);
                event.consume();
            }
        });

        cancelButton.setOnAction(e ->{
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(event ->{
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.showAndWait();
    }
    private void setLeagueItems(){
        ArrayList<String> leagueItemList = new ArrayList<>();
        for (League league: GraphicalUserInterface.getLeagueList()){
            leagueItemList.add(league.getName());
        }
        leagueItemList.add("Create");
        leagueSelector.setItems(FXCollections.observableList(leagueItemList));
    }

    private void setTeamItems(League league){
        ArrayList<String> teamItemList = new ArrayList<>();
        if(league.getTeamNames().isEmpty()){
            teamItemList.add("None");
        }
        else {
            teamItemList.addAll(league.getTeamNames());
        }
        teamItemList.add("Create");
        teamSelector.setItems(FXCollections.observableList(teamItemList));
    }

    private void createLeague(String text, Stage stage){
        if (!text.isBlank()){
            League league = new League(text, new ArrayList<>(List.of(QB, WR, WR, RB, RB, TE, FLEX, K)));
            GraphicalUserInterface.addLeague(league);
            setLeagueItems();
            leagueSelector.setValue(text);
            setTeamItems(league);
            teamSelector.setValue(league.getTeamNames().isEmpty() ? "None" : league.getTeamNames().getFirst());
            setDisable(false);
            stage.close();
        }
    }

    private void createTeam(String text, Stage stage, League league){
        if (!text.isBlank()){
            league.addTeam(text);
            setTeamItems(league);
            teamSelector.setValue(text);
            setDisable(false);
            stage.close();
        }
    }

    private League getLeagueByName(String leagueName){
        for (League league: GraphicalUserInterface.getLeagueList()){
            if (leagueName.equals(league.getName())){
                return league;
            }
        }
        return null;
    }
}