package edu.bsu.cs222.gui.playersView;

import edu.bsu.cs222.gui.ErrorModal;
import edu.bsu.cs222.gui.GraphicalUserInterface;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.*;

import static edu.bsu.cs222.model.Position.*;

public class PlayersViewController {
    @FXML private ImageView logoImageView;
    @FXML private Button reloadButton;
    @FXML private Button teamViewButton;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;

    private final ReadOnlyObjectWrapper<League.Team> currentTeam = new ReadOnlyObjectWrapper<>();

    private final ImageView reloadIcon = new ImageView(new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/reload_icon.png"))), 20, 20, true, true));
    private final Image logoImage = new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/myLeague_logo.png"))));

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
        listView.setCellFactory(_ -> new PlayersViewCell(this));
        positionFilter.setValue("All");
        teamFilter.setValue("All");
        leagueSelector.setValue("Default");

        reloadButton.setGraphic(reloadIcon);
        logoImageView.setImage(logoImage);

        managePlayersView(PlayerRetriever.getPlayerArrayList());
    }

    public void managePlayersView(ArrayList<Player> players) {
        ObservableList<Player> observableList = FXCollections.observableList(players);
        FilteredList<Player> filteredList = new FilteredList<>(observableList, _ -> true);
        listView.setItems(filteredList);
        setPositionsAndTeams(players);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> (Player player) -> {
            String searchText = searchField.getText();
            ArrayList<String> queries = (searchText == null || searchText.isBlank()) ? new ArrayList<>(): new ArrayList<>(Arrays.asList(searchText.toLowerCase().split("\\s+")));
            if (!runSearch(queries, player)) {return false;}

            String positionString = positionFilter.getValue();
            if (!positionString.isBlank() && !positionString.equals("All")){
                if (player.getPosition() != valueOf(positionString)) {return false;}
            }

            String team = teamFilter.getValue();
            if (!team.isBlank() && !team.equals("All")){
                return team.equals(player.getTeam());
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

        leagueSelector.valueProperty().addListener((_, oldVal, newVal) -> {
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

        teamSelector.valueProperty().addListener((_, oldVal, newVal) -> {
            if (oldVal != null && !oldVal.equals("Create") && !oldVal.isBlank()) {
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

        for (Position position: values()){
            positionFilter.getItems().add(position.toString());
        }
        positionFilter.getItems().remove("FLEX");

        teamFilter.getItems().addAll(teams);
    }

    public void openTeamView() throws IOException {
        GraphicalUserInterface.setRoot("/fxml_files/teamView/TeamView.fxml");
    }

    public void setDisable(boolean disable){
        leagueSelector.setDisable(disable);
        teamSelector.setDisable(disable);
        positionFilter.setDisable(disable);
        teamFilter.setDisable(disable);
        searchField.setDisable(disable);
        teamViewButton.setDisable(disable);
        reloadButton.setDisable(disable);
    }

    private void getLeaguePositions(Parent root, Stage creator){
        ArrayList<Position> teamPositions = new ArrayList<>();
        HashMap<Position, TextField> positonFieldMap = new HashMap<>();
        positonFieldMap.put(QB, (TextField) root.lookup("#qbField"));
        positonFieldMap.put(RB, (TextField) root.lookup("#rbField"));
        positonFieldMap.put(TE, (TextField) root.lookup("#teField"));
        positonFieldMap.put(WR, (TextField) root.lookup("#wrField"));
        positonFieldMap.put(K, (TextField) root.lookup("#kField"));
        positonFieldMap.put(FLEX, (TextField) root.lookup("#flexField"));

        TextField nameField = (TextField) root.lookup("#nameField");


        if (!nameField.getText().isBlank()){
            boolean validPositions = true;
            for (Position key : positonFieldMap.keySet()){
                if (positonFieldMap.get(key).getText().isBlank()){
                    try {
                        ErrorModal.throwErrorModal("Please ensure all field have valid position numbers", null);
                    } catch (IOException e) {
                        System.err.println("File not found");
                        System.exit(1);
                    }
                    validPositions = false;
                    break;
                }
                try {
                    for (int i = 0; i < Integer.parseInt(positonFieldMap.get(key).getText()); ++i){
                        teamPositions.add(key);
                    }
                } catch (NumberFormatException _){
                    try {
                        ErrorModal.throwErrorModal("Please ensure all field have valid position numbers", null);
                    } catch (IOException _) {
                        System.err.println("File not found");
                        System.exit(1);
                    }
                    validPositions = false;
                    break;
                }
            }
            if (validPositions){setLeagueCoefficients(nameField.getText(), teamPositions, creator);}
        }
    }

    private void leagueCreator() throws IOException {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("League Creator");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/LeagueCreatorModal.fxml"));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");

        createButton.setOnAction(_ -> getLeaguePositions(root, creator));

        createButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                getLeaguePositions(root, creator);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                leagueSelector.setValue(previousLeagueString);
                teamSelector.setValue(previousTeamString);
                setDisable(false);
                creator.close();
            }
        });

        cancelButton.setOnAction(_ ->{
            leagueSelector.setValue(previousLeagueString);
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(_ ->{
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

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/TeamCreatorModal.fxml")));
        Parent root = loader.load();

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");
        TextField nameField = (TextField) root.lookup("#nameField");

        createButton.setOnAction(_ -> {
            if (!nameField.getText().isBlank()){
                createTeam(nameField.getText(), creator, league);
            }
        });

        creator.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !nameField.getText().isBlank()){
                createTeam(nameField.getText(), creator, league);
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                teamSelector.setValue(previousTeamString);
                setDisable(false);
                creator.close();
            }
        });

        cancelButton.setOnAction(_ ->{
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(_ ->{
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

    private void setLeagueCoefficients(String name, ArrayList<Position> teamPositions, Stage stage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/SetScoringModal.fxml")));
            stage.getScene().setRoot(root);
        } catch (IOException e){
            System.err.println("File not found");
            System.exit(1);
        }

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createLeague");

        Parent finalRoot = root;
        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                createLeague(name, teamPositions, stage, finalRoot);
            }
        });

        createButton.setOnAction(_ -> createLeague(name, teamPositions, stage, finalRoot));

        stage.setOnCloseRequest(_ ->{
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            stage.close();
        });

        cancelButton.setOnAction(_ ->{
            teamSelector.setValue(previousTeamString);
            setDisable(false);
            stage.close();
        });
    }

    private void createLeague(String name, ArrayList<Position> teamPositions, Stage stage, Parent root) {
        HashMap<String, Double> coefficientMap = new HashMap<>();
        ArrayList<TextField> coefficientTextFields = new ArrayList<>();

        coefficientTextFields.add((TextField) root.lookup("#rushYards"));
        coefficientTextFields.add((TextField) root.lookup("#recYards"));
        coefficientTextFields.add((TextField) root.lookup("#passYards"));
        coefficientTextFields.add((TextField) root.lookup("#rushTds"));
        coefficientTextFields.add((TextField) root.lookup("#recTds"));
        coefficientTextFields.add((TextField) root.lookup("#passTds"));
        coefficientTextFields.add((TextField) root.lookup("#receptions"));
        coefficientTextFields.add((TextField) root.lookup("#interceptions"));
        coefficientTextFields.add((TextField) root.lookup("#fumbles"));
        coefficientTextFields.add((TextField) root.lookup("#xpMade"));
        coefficientTextFields.add((TextField) root.lookup("#fgMade"));

        for (TextField coefficientTextField : coefficientTextFields){
            if (coefficientTextField.getText().isBlank()){
                try {
                    ErrorModal.throwErrorModal("Please ensure all fields have valid coefficients", null);
                    return;

                } catch (IOException err){
                    System.err.println("File not found");
                    System.exit(1);
                }
            }
            try {
                coefficientMap.put(coefficientTextField.getId(), Double.parseDouble(coefficientTextField.getText()));
            } catch (NumberFormatException e) {
                try {
                    ErrorModal.throwErrorModal("Please ensure all fields have valid coefficients", null);
                    return;

                } catch (IOException err){
                    System.err.println("File not found");
                    System.exit(1);
                }
            }
        }

        System.out.println(coefficientMap);
        League league = new League(name, teamPositions, coefficientMap);
        GraphicalUserInterface.addLeague(league);
        setLeagueItems();
        leagueSelector.setValue(name);
        setTeamItems(league);
        teamSelector.setValue(league.getTeamNames().isEmpty() ? "None" : league.getTeamNames().getFirst());
        setDisable(false);
        stage.close();
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

    public void reloadPlayerList() throws IOException, InterruptedException {
        boolean networkError = PlayerRetriever.createAndSavePlayerListFromApi();

        if (networkError){
            ErrorModal.throwErrorModal("Network Error", null);
        }
        else {
            GraphicalUserInterface.setRoot("/fxml_files/playersView/PlayersView.fxml");
        }
    }
}