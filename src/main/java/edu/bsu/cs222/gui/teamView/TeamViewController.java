package edu.bsu.cs222.gui.teamView;

import edu.bsu.cs222.gui.ErrorModal;
import edu.bsu.cs222.gui.GraphicalUserInterface;
import edu.bsu.cs222.model.League;
import edu.bsu.cs222.model.Player;
import edu.bsu.cs222.model.Position;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

public class TeamViewController {

    @FXML private Label teamScore;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> positionFilter;
    @FXML private TextField searchField;

    private final ObservableList<Player> playerList = FXCollections.observableArrayList();
    private final FilteredList<Player> filteredList = new FilteredList<>(playerList, _ -> true);

    private final Image logoImage = new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/myLeague_logo.png"))));


    @FXML
    public void initialize() {
        setLeagueItems();
        leagueSelector.setValue(leagueSelector.getItems().getFirst());

        setTeamItems(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())));
        teamSelector.setValue(teamSelector.getItems().contains("None") ? "None" : teamSelector.getItems().getFirst());
        boolean networkError = false;
        if (!teamSelector.getValue().equals("None")){
            double score = 0;
            for (Player player : getCurrentTeam().getPlayerMap().keySet()){
                try {
                    player.setStatsWithAPI();
                    score += player.getWeekScore(getCurrentTeam().getCoefficientMap());
                } catch (Exception _){
                    ErrorModal.throwErrorModal("Network Error", null);
                    networkError = true;
                    break;
                }
            }
            teamScore.setText(String.format("%.1fpts", score));
        }

        listView.setFixedCellSize(70);
        boolean finalNetworkError = networkError;
        listView.setCellFactory(_ -> new TeamViewCell(this, finalNetworkError));
        listView.setItems(filteredList);
        logoImageView.setImage(logoImage);

        positionFilter.getItems().add("All");
        for (Position position: GraphicalUserInterface.getLeagueList().getFirst().getTeamPositions()){
            positionFilter.getItems().add(position.toString());
        }
        positionFilter.setValue("All");

        League leagueOnStart = Objects.requireNonNull(getLeagueByName(leagueSelector.getValue()));
        String selectedTeam = leagueOnStart.getTeamNames().isEmpty() ? "None" : leagueOnStart.getTeamNames().getFirst();
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

        teamSelector.valueProperty().addListener((_, _, _) -> {
            if (getCurrentTeam() != null){
                try {
                    calculateScore();
                } catch (Exception _) {
                    teamScore.setText("0.0pts");
                }
            }
            loadTeamPlayers();
        });

        leagueSelector.valueProperty().addListener((_, _, newVal) -> {
            positionFilter.getItems().clear();
            positionFilter.getItems().add("All");
            for (Position position: Objects.requireNonNull(getLeagueByName(newVal)).getTeamPositions()){
                positionFilter.getItems().add(position.toString());
            }

            setTeamItems(Objects.requireNonNull(getLeagueByName(newVal)));
            teamSelector.setValue(teamSelector.getItems().contains("None") ? "None" : teamSelector.getItems().getFirst());
            loadTeamPlayers();
            if (getCurrentTeam() != null){
                try {
                    calculateScore();
                } catch (Exception _) {
                    teamScore.setText("0.0pts");
                }
            }
        });
    }


    private void calculateScore() {
        double score = 0;
        for (Player player : getCurrentTeam().getPlayerMap().keySet()){
            try {
                player.setStatsWithAPI();
                score += player.getWeekScore(getCurrentTeam().getCoefficientMap());
            } catch (Exception _) {
                ErrorModal.throwErrorModal("Network Error", null);
            }
        }
        teamScore.setText(String.format("%.1fpts", score));
    }
    private void loadTeamPlayers() {
        League.Team team = getCurrentTeam();
        if (team == null) {
            playerList.setAll(List.of());
        }
        else {
            playerList.setAll(getCurrentTeam().getPlayerMap().keySet());
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

    public void openPlayersView() {
        GraphicalUserInterface.setRoot("/fxml_files/playersView/PlayersView.fxml");
    }

    public League.Team getCurrentTeam() {
        String teamString = teamSelector.getValue();
        return (teamString == null || teamString.equals("None") ? null : Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(teamString));
    }
}
