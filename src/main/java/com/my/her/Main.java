package com.my.her;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private final GameMode gameMode = new GameMode();

    private final GameBoardRepresentation gameBoard = new GameBoardRepresentation();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);

    private final MouseEvent clickEvent = new MouseEvent(
            MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
            true, true, true, true, true, true, true, true, true, true, null
    );

    private final MouseEvent releaseEvent = new MouseEvent(
            MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
            true, true, true, true, true, true, true, true, true, true, null
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage){

        //Create the 2 scenes
        Scene scene1, scene2;

        //Scene1
        Group root = new Group();
        scene1 = new Scene(root, 1000,800, Color.ANTIQUEWHITE);
        Image icon = new Image("pawn.png");

        stage.getIcons().add(icon);
        stage.setTitle("Hexapawn");

        //Set text in Scene1
        Text text = new Text();
        text.setText("HEXAPAWN");
        text.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, 100));
        text.setFill(Color.DARKOLIVEGREEN);

        //Hbox to hold text in Scene1
        HBox titleName = new HBox();
        titleName.setPadding(new Insets(100,0,30,100));
        titleName.setAlignment(Pos.CENTER);
        titleName.getChildren().add(text);

        //Make Board
        GridPane board = new GridPane();
        board.setPrefSize(600,380);

        //Label, hbox, and dropdown for Game Mode dropdown
        Label gameModeLabel = new Label(" Game Mode:");
        gameModeLabel.setTextFill(Color.DARKOLIVEGREEN);
        gameModeLabel.setFont(Font.font("Times New Roman",FontWeight.BOLD, 40));
        ComboBox gameModeDwn = new ComboBox();
        gameModeDwn.getItems().addAll("Slow", "Fast", "Auto");
        gameModeDwn.setStyle("-fx-border-color: #556b2f; -fx-border-width: 3px;");
        gameModeDwn.setMaxWidth(500);
        gameModeDwn.setMaxHeight(20);
        HBox gameModeHbox = new HBox();
        gameModeHbox.getChildren().addAll(gameModeLabel, gameModeDwn);
        gameModeHbox.setPadding(new Insets(30,0,30,30));
        gameModeHbox.setSpacing(40);
        gameModeHbox.setAlignment(Pos.CENTER);
        gameModeDwn.setStyle("-fx-border-color: #556b2f; -fx-border-width: 3px; -fx-font-size: 20px; -fx-text-fill: #556b2f;");

        gameModeDwn.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            // Set the size and color
                            setStyle("-fx-font-size: 20px; -fx-text-fill: #556b2f;");  // Modify the values as needed
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        gameModeDwn.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setStyle("-fx-font-size: 20px; -fx-text-fill: #556b2f;");
                } else {
                    setText(null);
                }
            }
        });
        VBox arrows = new VBox();
        gameModeDwn.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleGameModeChange((String) newValue);
            if (!gameMode.getGameSettings().getMode().equals("Slow")) {
                arrows.setOpacity(0);
                executor.scheduleWithFixedDelay(() -> {
                    if ((gameMode.getCurrentStateSecond() != null || gameMode.getCurrentState() != null)
                            && gameMode.getGameSettings().isCanMove()) {
                        if (gameMode.getGameSettings().getMode().equals("Auto") && gameBoard.getCurrentPlayer().equals("W")
                                && gameMode.getGameSettings().getRounds() > 0) {
                            Platform.runLater(() -> onClicked(board, gameMode.getCurrentStateSecond().randomBead()));
                        } else if (gameBoard.getCurrentPlayer().equals("B")) {
                            Platform.runLater(() -> onClicked(board, gameMode.getCurrentState().randomBead()));
                            if (gameMode.getGameSettings().getMode().equals("Fast"))
                                gameMode.getGameSettings().setCanMove(false);
                        }
                    }
                }, gameMode.getGameSettings().getSpeed(), gameMode.getGameSettings().getSpeed(), TimeUnit.MILLISECONDS);
            }
        });

        Label roundsNumberLabel = new Label("Number of Rounds:");
        roundsNumberLabel.setTextFill(Color.DARKOLIVEGREEN);
        roundsNumberLabel.setFont(Font.font("Times New Roman",FontWeight.BOLD, 40));
        TextField roundNumberTxtField = new TextField();
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (!change.getControlNewText().matches("\\d*")){
                return null;
            } else {
                if(!roundNumberTxtField.getText().equals("")) {
                    gameMode.getGameSettings().setRounds(Integer.parseInt(roundNumberTxtField.getText()));
                }
                return change;
            }
        });
        roundNumberTxtField.setTextFormatter(formatter);
        roundNumberTxtField.setPrefHeight(55);
        roundNumberTxtField.setPrefWidth(90);
        roundNumberTxtField.setStyle("-fx-text-fill: rgb(128,128,128);");
        roundNumberTxtField.setStyle("-fx-border-color: #556b2f; -fx-border-width: 3px; -fx-font-size: 20px; -fx-text-fill: #556b2f;");
        HBox roundNumberHbox = new HBox();
        roundNumberHbox.getChildren().addAll(roundsNumberLabel, roundNumberTxtField);
        roundNumberHbox.setAlignment(Pos.CENTER);
        roundNumberHbox.setSpacing(20);




        //Start Button
        Button Start = new Button("START");
        Start.setFont(Font.font("Times New Roman", FontWeight.BOLD,35));
        Start.setTextFill(Color.DARKOLIVEGREEN);
        Start.setAlignment(Pos.BOTTOM_CENTER);
        Start.setStyle("-fx-border-color: #556b2f; -fx-border-width: 5px;");
        Start.setOnMouseReleased(clickEvent -> start());


        HBox startButton = new HBox();
        startButton.setAlignment(Pos.CENTER);
        startButton.getChildren().addAll(Start);
        startButton.setPadding(new Insets(50,0,0,100));

        //Vbox for scene1
        VBox startPage = new VBox();
        startPage.getChildren().addAll(titleName, gameModeHbox,roundNumberHbox, startButton);

        //Scene2
        VBox layout2 = new VBox(20);
        scene2= new Scene(layout2,1000,800);

        //Label for Scene 2

        Label label2 = new Label("HER Next Move");
        label2.setFont(Font.font("Times New Roman", FontWeight.BOLD,35));
        label2.setTextFill(Color.DARKOLIVEGREEN);
        label2.setPadding(new Insets(80,10,10,90));

        //Hbox for label 2
        HBox labelScene2 = new HBox(label2);

        //Hbox for moves image
        GridPane moveHERForward = new GridPane();
        moveHERForward.setPrefSize(300,200);
        moveHERForward.setAlignment(Pos.TOP_LEFT);
        moveHERForward.setStyle("-fx-border-color: #556b2f; -fx-border-width: 5px;");
        gameMode.setBoard(moveHERForward);

        HBox movePatternBox = new HBox();
        movePatternBox.setAlignment(Pos.CENTER_LEFT);
        movePatternBox.getChildren().add(moveHERForward);

        HBox firstPartLayout = new HBox();
        firstPartLayout.getChildren().addAll(labelScene2,movePatternBox);
        firstPartLayout.setSpacing(50);

        //Red Arrow button, and Label
        Button redArrow = new Button();
        Image redArrowImage = new Image ("redArrow.png");
        ImageView imageRedArrow = new ImageView(redArrowImage);
        imageRedArrow.setFitHeight(50);
        imageRedArrow.setFitWidth(50);
        redArrow.setGraphic(imageRedArrow);
        redArrow.setOnMouseClicked(event -> onClicked(board, "Red"));

        Label redArrowPctg = new Label();
        gameMode.addArrows("Red", redArrowPctg);
        int redPercentagePlays = 0;
        String textRedArrow = "%";
        redArrowPctg.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
        redArrowPctg.setPadding(new Insets(0,0,0,5));
        redArrowPctg.setText(textRedArrow + redPercentagePlays);

        VBox redArrowImageLabel = new VBox();
        redArrowImageLabel.getChildren().addAll(redArrow,redArrowPctg);

        //Green Arrow Button, and label
        Button greenArrow = new Button();
        Image greenArrowImage = new Image ("greenArrow.png");
        ImageView imageGreenArrow = new ImageView(greenArrowImage);
        imageGreenArrow.setFitHeight(50);
        imageGreenArrow.setFitWidth(50);
        greenArrow.setGraphic(imageGreenArrow);
        greenArrow.setOnMouseClicked(event -> onClicked(board, "Green"));

        Label greenArrowPctg = new Label();
        gameMode.addArrows("Green", greenArrowPctg);
        String textGreenArrow = "%";
        int greenPercentagePlays = 0;
        greenArrowPctg.setText(textGreenArrow + greenPercentagePlays);
        greenArrowPctg.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
        greenArrowPctg.setPadding(new Insets(0,0,0,5));

        VBox greenArrowImageLabel = new VBox();
        greenArrowImageLabel.getChildren().addAll(greenArrow,greenArrowPctg);

        //Blue Arrow Button and label

        Button blueArrow = new Button();
        Image blueArrowImage = new Image ("blueArrow.png");
        ImageView imageBlueArrow = new ImageView(blueArrowImage);
        imageBlueArrow.setFitHeight(50);
        imageBlueArrow.setFitWidth(50);
        blueArrow.setGraphic(imageBlueArrow);
        blueArrow.setOnMouseClicked(event -> onClicked(board, "Blue"));

        Label blueArrowPctg = new Label();
        gameMode.addArrows("Blue", blueArrowPctg);
        String textBlueArrow = "%";
        int bluePercentagePlays = 0;
        blueArrowPctg.setText(textBlueArrow + bluePercentagePlays);
        blueArrowPctg.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
        blueArrowPctg.setPadding(new Insets(0,0,0,5));

        VBox blueArrowImageLabel = new VBox();
        blueArrowImageLabel.getChildren().addAll(blueArrow,blueArrowPctg);

        //Yellow Arrow Button and label

        Button yellowArrow = new Button();
        Image yellowArrowImage = new Image ("yellowArrow.png");
        ImageView imageYellowArrow = new ImageView(yellowArrowImage);
        imageYellowArrow.setFitHeight(50);
        imageYellowArrow.setFitWidth(50);
        yellowArrow.setGraphic(imageYellowArrow);
        yellowArrow.setOnMouseClicked(event -> onClicked(board, "Yellow"));

        Label yellowArrowPctg = new Label();
        gameMode.addArrows("Yellow", yellowArrowPctg);
        String textYellowArrow = "%";
        int yellowPercentagePlays = 0;
        yellowArrowPctg.setText(textYellowArrow + yellowPercentagePlays);
        yellowArrowPctg.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
        yellowArrowPctg.setPadding(new Insets(0,0,0,5));

        VBox yellowArrowImageLabel = new VBox();
        yellowArrowImageLabel.getChildren().addAll(yellowArrow,yellowArrowPctg);

        //Random Button and label

        Button random = new Button();
        Image randomImage = new Image ("question.png");
        ImageView imageRandom = new ImageView(randomImage);
        imageRandom.setFitHeight(50);
        imageRandom.setFitWidth(50);
        random.setGraphic(imageRandom);
        random.setOnMouseClicked(event -> onClickedRandom(board));

        Label randomPctg = new Label();
        String textRandom = "Computer\nchoose";
        randomPctg.setText(textRandom);
        randomPctg.setFont(Font.font("Times New Roman", FontWeight.BOLD,10));
        randomPctg.setPadding(new Insets(0,0,0,5));

        VBox randomImageLabel = new VBox();
        randomImageLabel.getChildren().addAll(random,randomPctg);

        arrows.getChildren().addAll(redArrowImageLabel, greenArrowImageLabel,blueArrowImageLabel,
                yellowArrowImageLabel, randomImageLabel);
        arrows.setPadding(new Insets(0,0,5,10));
        arrows.setSpacing(25);

        VBox capturedPiecesDisplay = gameBoard.createCapturedPiecesDisplay();

        //draw dask
        gameBoard.createBoard(board);

        HBox boardHbox = new HBox();
        boardHbox.getChildren().addAll(arrows, board, capturedPiecesDisplay);
        boardHbox.setSpacing(50);

        layout2.getChildren().addAll(firstPartLayout,boardHbox);
        layout2.setSpacing(100);

        //Make the start button change scenes.
        Start.setOnAction(event -> stage.setScene(scene2));

        root.getChildren().add(startPage);
        stage.setScene(scene1);
        stage.setResizable(true);
        stage.show();

    }
    private void onClickedRandom(GridPane board) {
        onClicked(board, gameMode.getCurrentState().randomBead());
    }
    private void onClicked(GridPane board, String color){
        List<Node> childrenCopy = new ArrayList<>(board.getChildren());
        Set<Map.Entry<String, Point2D[]>> set = new HashSet<>();
        if(gameBoard.getCurrentPlayer().equals("W") && gameMode.getGameSettings().getMode().equals("Auto"))
            set = gameMode.getCurrentStateSecond().pair.entrySet();
        else if(gameBoard.getCurrentPlayer().equals("B"))
            set = gameMode.getCurrentState().pair.entrySet();
        for (Map.Entry<String, Point2D[]> entry: set){
            if(entry.getKey().equals(color)){
                Point2D p1 = entry.getValue()[0];
                Point2D p2 = entry.getValue()[1];
                for (Node node : childrenCopy) {
                    if (node instanceof StackPane) {
                        int rowIndex = GridPane.getRowIndex(node);
                        int colIndex = GridPane.getColumnIndex(node);

                        if (rowIndex == p1.getX() && colIndex == p1.getY()) {
                            StackPane stackPane = (StackPane) node;
                            stackPane.fireEvent(clickEvent);
                        }
                    }
                }
                for (Node node : childrenCopy) {
                    if (node instanceof StackPane) {
                        int rowIndex = GridPane.getRowIndex(node);
                        int colIndex = GridPane.getColumnIndex(node);
                        if (rowIndex == p2.getX() && colIndex == p2.getY()) {
                            StackPane stackPane = (StackPane) node;
                            stackPane.fireEvent(releaseEvent);
                        }
                    }
                }
            }
        }
    }
    private void start(){
        if(gameMode.getGameSettings().getMode().equals("Auto"))
            gameMode.getGameSettings().setCanMove(true);
    }
    private void handleGameModeChange(String mode) {
        if (mode == null) {
            return;
        }
        gameMode.setGameSettings(new GameSettings(mode));
        gameBoard.setGameMode(gameMode);
    }
}