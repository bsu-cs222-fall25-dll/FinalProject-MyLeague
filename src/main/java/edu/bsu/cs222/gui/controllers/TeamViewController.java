package edu.bsu.cs222.gui.controllers;

import edu.bsu.cs222.gui.GraphicalUserInterface;
import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.Position;
import edu.bsu.cs222.gui.ErrorModal;
import edu.bsu.cs222.gui.TeamViewCell;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import java.io.IOException;
import java.util.*;

public class TeamViewController {

    @FXML private Button scoreButton;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> positionFilter;
    @FXML private TextField searchField;

    private final ObservableList<Player> playerList = FXCollections.observableArrayList();
    private final FilteredList<Player> filteredList = new FilteredList<>(playerList, p -> true);

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(lb -> new TeamViewCell(this));
        listView.setItems(filteredList);

        setLeagueItems();
        leagueSelector.setValue(GraphicalUserInterface.getLeagueList().getFirst().getName());

        setTeamItems(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())));
        teamSelector.setValue(teamSelector.getItems().contains("None") ? "None" : teamSelector.getItems().getFirst());

        positionFilter.getItems().add("All");
        for (Position position: GraphicalUserInterface.getLeagueList().getFirst().getTeamPositions()){
            positionFilter.getItems().add(position.toString());
        }
        positionFilter.setValue("All");

        League defaultLeague = getLeagueByName("Default");
        assert defaultLeague != null;
        String selectedTeam = defaultLeague.getTeamNames().isEmpty() ? "None" : defaultLeague.getTeamNames().getFirst();
        teamSelector.setValue(selectedTeam);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player player) -> {
                if (player == null) {return false;}

                String searchText = searchField.getText();
                ArrayList<String> queries = (searchText == null || searchText.isBlank()) ? new ArrayList<>(): new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));
                if (!runSearch(queries, player)) {return false;}

                String positionString = positionFilter.getValue();
                if (positionString != null && !positionString.equals("All") && !positionString.isBlank()){
                    League.Team currentTeam = getCurrentTeam();
                    if (currentTeam == null) {return false;}
                    return currentTeam.getPlayerMap().get(player) == Position.valueOf(positionString);
                }
                return true;
            },
                searchField.textProperty(),
                positionFilter.valueProperty()
        ));


        loadTeamPlayers();

        teamSelector.valueProperty().addListener((obs, oldVal, newVal) -> loadTeamPlayers());

        leagueSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            positionFilter.getItems().clear();
            positionFilter.getItems().add("All");
            for (Position position: Objects.requireNonNull(getLeagueByName(newVal)).getTeamPositions()){
                positionFilter.getItems().add(position.toString());
            }

            setTeamItems(Objects.requireNonNull(getLeagueByName(newVal)));
            teamSelector.setValue(teamSelector.getItems().contains("None") ? "None" : teamSelector.getItems().getFirst());
            loadTeamPlayers();
        });
    }

    private void loadTeamPlayers() {
        League.Team team = getCurrentTeam();
        if (team == null) {
            playerList.setAll(List.of());
        }
        else {
            playerList.setAll(getCurrentTeam().getPlayerMap().keySet());
            if (team.getCalculatedScore() == -1){
                scoreButton.setText("Calculate");
            }
            else {
                scoreButton.setText(team.getCalculatedScore() + "pts");
            }
        }
    }

    public void removePlayerFromList(Player player){
        playerList.remove(player);
    }

    private boolean runSearch(ArrayList<String> queries, Player player) {
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

    private League getLeagueByName(String leagueName){
        for (League league: GraphicalUserInterface.getLeagueList()){
            if (league.getName().equals(leagueName)){
                return league;
            }
        }
        return null;
    }

    private void setLeagueItems(){
        ArrayList<String> leagueItemList = new ArrayList<>();
        for (League league: GraphicalUserInterface.getLeagueList()){
            leagueItemList.add(league.getName());
        }
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
        teamSelector.setItems(FXCollections.observableList(teamItemList));
    }

    public void openPlayersView() throws IOException {
        GraphicalUserInterface.setRoot("/FXML_Files/PlayersView.fxml");
    }

    public League.Team getCurrentTeam() {
        String teamString = teamSelector.getValue();
        return (teamString.equals("None") ? null : Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(teamString));
    }

    public void calculateTeamScore() throws IOException {
        League.Team team = getCurrentTeam();
        if (team == null){
            ErrorModal.throwErrorModal("Select a team", null);
        }
        else if (team.getPlayerMap().isEmpty()){
            ErrorModal.throwErrorModal("Add players to team", null);
        }
        else{
            int score = 10;
            team.setCalculatedScore(score);
            scoreButton.setText(score + "pts");
        }
    }
}
