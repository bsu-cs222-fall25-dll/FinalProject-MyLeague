package edu.bsu.cs222.gui.playersView;

import edu.bsu.cs222.gui.ConfirmationModal;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.*;

import static edu.bsu.cs222.model.Position.*;

public class PlayersViewController {
    @FXML private Button deleteLeagueButton;
    @FXML private Button saveButton;
    @FXML private Button deleteTeamButton;
    @FXML private Button editButton;
    @FXML private ImageView logoImageView;
    @FXML private Button reloadButton;
    @FXML private Button teamViewButton;
    @FXML private ComboBox<String> teamSelector;
    @FXML private ComboBox<String> leagueSelector;
    @FXML private TextField searchField;
    @FXML private ListView<Player> listView;
    @FXML private ComboBox<String> teamFilter;
    @FXML private ComboBox<String> positionFilter;

    private String previousLeagueString;
    private String previousTeamString;

    private final ReadOnlyObjectWrapper<League.Team> currentTeam = new ReadOnlyObjectWrapper<>();

    private final ImageView reloadIcon = new ImageView(new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/reload_icon.png"))), 20, 20, true, true));
    private final ImageView editIcon = new ImageView(new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/edit_icon.png"))), 20, 20, true, true));
    private final ImageView saveIcon = new ImageView(new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/save_icon.png"))), 20, 20, true, true));

    private final Image logoImage = new Image((Objects.requireNonNull(getClass().getResourceAsStream("/images/myLeague_logo.png"))));

    @FXML
    public void initialize() {
        listView.setFixedCellSize(70);
        listView.setCellFactory(_ -> new PlayersViewCell(this));
        positionFilter.setValue("All");
        teamFilter.setValue("All");

        reloadButton.setGraphic(reloadIcon);
        editButton.setGraphic(editIcon);
        saveButton.setGraphic(saveIcon);

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
        leagueSelector.setValue(leagueSelector.getItems().getFirst());
        previousLeagueString = leagueSelector.getValue();

        setTeamItems(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())));
        teamSelector.setValue(teamSelector.getItems().getFirst());
        previousTeamString = leagueSelector.getValue();

        if (Objects.equals(teamSelector.getValue(), "None") || teamSelector.getValue() == null){
            deleteTeamButton.setDisable(true);
            currentTeam.set(null);
        }

        leagueSelector.valueProperty().addListener((_, oldVal, newVal) -> {
            if (Objects.equals(newVal, "Create")) {
                teamSelector.getSelectionModel().clearSelection();
                setDisable(true);
                leagueCreator();
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
                    setDisable(true);
                    teamCreator(getLeagueByName(leagueSelector.getValue()));
                } else {
                    currentTeam.set(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(newVal));
                    deleteTeamButton.setDisable(false);
                }
            }else {
                deleteTeamButton.setDisable(true);
                currentTeam.set(null);
            }
        });
    }

    // League creation and editing
    private void leagueCreator() {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("League Creator");
        Parent root = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/LeagueCreatorModal.fxml"));
        try {
            root = loader.load();
        }
        catch (IOException _) {
            System.err.println("LeagueCreatorModal.fxml not found");
            System.exit(1);
        }

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");

        Parent finalRoot = root;
        createButton.setOnAction(_ -> setLeaguePositions(finalRoot, creator));

        createButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                event.consume();
                setLeaguePositions(finalRoot, creator);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
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

    private void setLeaguePositions(Parent root, Stage creator){
        TextField nameField = (TextField) root.lookup("#nameField");
        if (nameField != null){
            String name = nameField.getText();
            if (name.isBlank()){
                ErrorModal.throwErrorModal("Please enter a name", null);
                return;
            }
            if (getLeagueByName(name) != null){
                ErrorModal.throwErrorModal("Please enter a unique name", null);
                return;
            }
        }

        ArrayList<Position> teamPositions = new ArrayList<>();
        HashMap<Position, TextField> positonFieldMap = new HashMap<>();
        positonFieldMap.put(QB, (TextField) root.lookup("#qbField"));
        positonFieldMap.put(RB, (TextField) root.lookup("#rbField"));
        positonFieldMap.put(TE, (TextField) root.lookup("#teField"));
        positonFieldMap.put(WR, (TextField) root.lookup("#wrField"));
        positonFieldMap.put(K, (TextField) root.lookup("#kField"));
        positonFieldMap.put(FLEX, (TextField) root.lookup("#flexField"));

        boolean validPositions = true;
        for (Position key : positonFieldMap.keySet()){
            if (positonFieldMap.get(key).getText().isBlank()){
                ErrorModal.throwErrorModal("Please ensure all field have valid position numbers", null);
                validPositions = false;
                break;
            }
            try {
                for (int i = 0; i < Integer.parseInt(positonFieldMap.get(key).getText()); ++i){
                    teamPositions.add(key);
                }
            } catch (NumberFormatException _){
                ErrorModal.throwErrorModal("Please ensure all field have valid position numbers", null);
                validPositions = false;
                break;
            }
        }
        if (validPositions){
            if (nameField == null){
                setLeagueCoefficients(null, teamPositions, creator);
            }
            else {
                setLeagueCoefficients(nameField.getText(), teamPositions, creator);
            }
        }
    }

    private void setLeagueCoefficients(String name, ArrayList<Position> teamPositions, Stage stage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/SetScoringModal.fxml")));
            stage.getScene().setRoot(root);
        } catch (IOException _){
            System.err.println("SetScoringModal.fxml not found");
            System.exit(1);
        }

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createLeague");

        if (name == null){
            createButton.setText("Edit");
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

            for (TextField textField : coefficientTextFields){
                textField.setText(String.valueOf(Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getCoefficientMap().get(textField.getId())));
            }
        }



        Parent finalRoot = root;
        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                event.consume();
                createOrEditLeague(name, teamPositions, stage, finalRoot);
            }
        });

        createButton.setOnAction(_ -> createOrEditLeague(name, teamPositions, stage, finalRoot));

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

    private void createOrEditLeague(String name, ArrayList<Position> teamPositions, Stage stage, Parent root) {
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
                ErrorModal.throwErrorModal("Please ensure all fields have valid coefficients", null);
                return;
            }
            try {
                coefficientMap.put(coefficientTextField.getId(), Double.parseDouble(coefficientTextField.getText()));
            } catch (NumberFormatException _) {
                ErrorModal.throwErrorModal("Please ensure all fields have valid coefficients", null);
                return;
            }
        }

        if (name != null){
            League league = new League(name, teamPositions, coefficientMap);
            GraphicalUserInterface.addLeague(league);
            setLeagueItems();
            leagueSelector.setValue(name);
            setTeamItems(league);
            teamSelector.setValue("None");
        }
        else {
            boolean lessPositions = false;
            ArrayList<Position> tempList = new ArrayList<>(teamPositions);
            League league = Objects.requireNonNull(getLeagueByName(leagueSelector.getValue()));
            for (Position position: league.getTeamPositions()){
               if(!tempList.remove(position)) {
                   lessPositions = true;
                   break;
               }
            }

            boolean confirmed = true;
            if (lessPositions){
                confirmed = ConfirmationModal.throwConfirmationModal("Due to lowering the number of positons some players from teams may be removed.\n Are you sure? (Score coefficients will be kept)");
            }

            if (confirmed){
                league.setTeamPositions(teamPositions, lessPositions);
            }
            league.setCoefficientMap(coefficientMap);

        }
        setDisable(false);
        stage.close();
    }

    @FXML private void editLeague(){
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("League Editor");
        Parent root = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/LeagueEditorModal.fxml"));
        try {
            root = loader.load();
        }
        catch (IOException _) {
            System.err.println("LeagueEditorModal.fxml not found");
            System.exit(1);
        }

        Label leagueLabel = (Label) root.lookup("#leagueLbl");
        leagueLabel.setText(String.format("Editing League: %s", leagueSelector.getValue()));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");

        HashMap<Position, Integer> positionMap = Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getPositionCountMap();

        ((TextField) root.lookup("#qbField")).setText(String.valueOf(positionMap.get(QB)));
        ((TextField) root.lookup("#rbField")).setText(String.valueOf(positionMap.get(RB)));
        ((TextField) root.lookup("#teField")).setText(String.valueOf(positionMap.get(TE)));
        ((TextField) root.lookup("#wrField")).setText(String.valueOf(positionMap.get(WR)));
        ((TextField) root.lookup("#kField")).setText(String.valueOf(positionMap.get(K)));
        ((TextField) root.lookup("#flexField")).setText(String.valueOf(positionMap.get(FLEX)));

        creator.setScene(new Scene(root));

        Parent finalRoot = root;

        createButton.setOnAction(_ -> setLeaguePositions(finalRoot, creator));

        createButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                event.consume();
                setLeaguePositions(finalRoot, creator);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                setDisable(false);
                creator.close();
            }
        });

        cancelButton.setOnAction(_ ->{
            setDisable(false);
            creator.close();
        });

        creator.setOnCloseRequest(_ ->{
            setDisable(false);
            creator.close();
        });

        creator.showAndWait();
    }

    // Team creation
    private void teamCreator(League league) {
        Stage creator = new Stage();
        creator.initModality(Modality.APPLICATION_MODAL);
        creator.setTitle("Team Creator");
        Parent root = null;

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml_files/playersView/teamAndLeagueCreation/TeamCreatorModal.fxml")));
        try {
            root = loader.load();
        }
        catch (IOException _){
            System.err.println("TeamCreatorModal.fxml not found");
            System.exit(1);
        }

        creator.setScene(new Scene(root));

        Button cancelButton = (Button) root.lookup("#cancelButton");
        Button createButton = (Button) root.lookup("#createButton");
        TextField nameField = (TextField) root.lookup("#nameField");

        createButton.setOnAction(_ -> createTeam(nameField.getText(), creator, league));

        creator.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                event.consume();
                createTeam(nameField.getText(), creator, league);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
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

    private void createTeam(String text, Stage stage, League league){
        if (text.isBlank()){
            ErrorModal.throwErrorModal("Please enter a name", null);
            return;
        }

        if (league.getTeamNames().contains(text)){
            ErrorModal.throwErrorModal("Please enter a unique name", null);
            return;
        }


        league.addTeam(text);
        setTeamItems(league);
        teamSelector.setValue(text);
        setDisable(false);
        stage.close();
    }

    // League and Team Deletion
    @FXML private void deleteLeague() {
        if (GraphicalUserInterface.getLeagueList().size() > 1){
            boolean delete = ConfirmationModal.throwConfirmationModal("Confirming will delete this league\nand all of its teams.\nAre you sure?");
            if (delete){
                League league = Objects.requireNonNull(getLeagueByName(leagueSelector.getValue()));
                if (league.getSaved()){
                    league.deleteLeague();
                }

                GraphicalUserInterface.removeLeague(league);
                leagueSelector.setValue(previousLeagueString);
            }
        } else {
            ErrorModal.throwErrorModal("Ensure there's at least two leagues,\nbefore attempting to delete one", null);
        }
    }

    @FXML private void deleteTeam(){
        boolean delete = ConfirmationModal.throwConfirmationModal("Confirming will delete this team\nAre you sure?");
        if (delete){
            League currentLeague = Objects.requireNonNull(getLeagueByName(leagueSelector.getValue()));
            currentLeague.removeTeam(getCurrentTeam());
            setTeamItems(currentLeague);
            teamSelector.setValue(teamSelector.getItems().getFirst());
        }
    }

    // Other OnAction methods
    @FXML private void saveLeague() {
        boolean save = ConfirmationModal.throwConfirmationModal("Saving this league will override previously saved leagues with the same name.\nAre you sure?");
        if (save){
            Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).saveLeague();
        }
    }

    @FXML private void reloadPlayerList() {

        try{
            PlayerRetriever.createAndSavePlayerListFromApi();
            GraphicalUserInterface.setRoot("/fxml_files/playersView/PlayersView.fxml");
        }
        catch (Exception _){
            ErrorModal.throwErrorModal("Network ddError", null);
        }
    }

    @FXML private void openTeamView() {
        GraphicalUserInterface.setRoot("/fxml_files/teamView/TeamView.fxml");
    }

    // Helper methods
    public void setDisable(boolean disable){
        leagueSelector.setDisable(disable);
        teamSelector.setDisable(disable);
        positionFilter.setDisable(disable);
        teamFilter.setDisable(disable);
        searchField.setDisable(disable);
        teamViewButton.setDisable(disable);
        reloadButton.setDisable(disable);
        editButton.setDisable(disable);
        saveButton.setDisable(disable);
        deleteLeagueButton.setDisable(disable);

        if (!disable){
            if (Objects.equals(teamSelector.getValue(), "None") || teamSelector.getValue() == null){
                deleteTeamButton.setDisable(true);
            }
        }
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

    // Setters
    private void setPositionsAndTeams(ArrayList<Player> players) {
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

    private League getLeagueByName(String leagueName){
        for (League league: GraphicalUserInterface.getLeagueList()){
            if (leagueName.equals(league.getName())){
                return league;
            }
        }
        return null;
    }

    // Current Team Listener methods
    public League.Team getCurrentTeam() {
        if (teamSelector.getValue().equals("None")){
            return null;
        }
        return Objects.requireNonNull(getLeagueByName(leagueSelector.getValue())).getTeamByName(teamSelector.getValue());
    }

    public ReadOnlyObjectProperty<League.Team> currentTeamProperty() {
        return currentTeam.getReadOnlyProperty();
    }
}