package com.my.her;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
public class GameBoardRepresentation {
    // Constants are defined for fixed values to make the code clearer and easier to maintain.
    private static final int BOARD_HEIGHT = 3;
    private static final int BOARD_WEIGHT = 3;
    private static final Color EVEN_CELL_COLOR = Color.WHITE;
    private static final Color ODD_CELL_COLOR = Color.OLIVEDRAB;
    private static final Color BORDER_COLOR = Color.DARKOLIVEGREEN;
    private static final String WHITE_PAWN = "W";
    private static final String BLACK_PAWN = "B";
    private static final double BOARD_WIDTH = 600.0;
    private static final double BOARD_VERTICAL_HEIGHT = 380.0;

    private GridPane board;
    private String[][] boardState;
    private StackPane selectedCell = null;
    private int selectedX = -1;
    private int selectedY = -1;
    private String currentPlayer = WHITE_PAWN;
    private VBox capturedPiecesDisplay;
    private GameMode gameMode;

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void createBoard(GridPane board) {
        this.board = board;
        board.getChildren().clear();
        boardState = new String[BOARD_HEIGHT][BOARD_WEIGHT];

        double rectWidth = BOARD_WIDTH / BOARD_WEIGHT;
        double rectHeight = BOARD_VERTICAL_HEIGHT / BOARD_HEIGHT;
        Image whitePawnImage = loadImage("whitePawn.png");
        Image blackPawnImage = loadImage("blackPawn.png");

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WEIGHT; j++) {
                initializeBoardState(i, j);
                StackPane cell = createCell(i, j, rectWidth, rectHeight);
                if (i == 0) {
                    addPawnToCell(cell, whitePawnImage, rectWidth, rectHeight);
                } else if (i == 2) {
                    addPawnToCell(cell, blackPawnImage, rectWidth, rectHeight);
                }
                board.add(cell, j, i);
            }
        }
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        this.gameMode.setCurrentPlayer(currentPlayer);
        if (gameMode.getGameSettings().getMode().equals("Auto"))
            this.gameMode.setStateBoard(boardState);
    }

    private void initializeBoardState(int i, int j) {
        // Sets the state of the cell based on its row.
        if (i == 0) {
            boardState[i][j] = WHITE_PAWN;
        } else if (i == 2) {
            boardState[i][j] = BLACK_PAWN;
        } else {
            boardState[i][j] = null;
        }
    }
    private StackPane createCell(int i, int j, double width, double height) {
        // Creates a visual cell with a colored rectangle.
        StackPane cell = new StackPane();
        Rectangle rect = new Rectangle(width, height);
        // Uses modulo to alternate cell colors.
        rect.setFill((i + j) % 2 == 0 ? EVEN_CELL_COLOR : ODD_CELL_COLOR);
        rect.setStroke(BORDER_COLOR);
        cell.getChildren().add(rect);
        //Mouse events to handle piece selection and movement
        cell.setOnMouseClicked(event -> handleCellClick(i,j,cell));
        cell.setOnMouseReleased(event -> handleCellRelease(i,j,cell));
        return cell;
    }

    private void handleCellRelease(int i, int j, StackPane cell) {
        if (selectedCell == null)return;

        if (isValidMove(selectedY,selectedX,i,j)){
            capturePawn(i,j);
            if(!gameMode.makeStep(selectedY, selectedX, i, j)){
                stepsIsOver();
                return;
            }
            boardState[i][j] = boardState[selectedY][selectedX];
            boardState[selectedY][selectedX] = null;
            movePawn(selectedCell, cell);
            selectedCell = null;
            selectedX = -1;
            selectedY = -1;
            switchPlayer();
            gameMode.setCurrentPlayer(currentPlayer);
            gameMode.setStateBoard(boardState);
            if(currentPlayer.equals("B")) {
                gameMode.getGameSettings().setCanMove(true);
                gameMode.addProbabilitiesArrow();
            }
            if (!hasValidMoves(currentPlayer)){
                stepsIsOver();
                return;
            }
            String winner = checkWinner();
            if (winner != null) {
                setPointWinner(winner);
                System.out.println(winner);
                resetBoard();
            }
        }
    }
    private void stepsIsOver(){
        String loser = currentPlayer;
        String winner = loser.equals(WHITE_PAWN) ? BLACK_PAWN : WHITE_PAWN;
        setPointWinner(winner);
        System.out.println(winner);
        resetBoard();
    }
    private void setPointWinner(String winner){
        if(gameMode.getGameSettings().getMode().equals("Auto")){
            if(winner.equals("W")) {
                if(gameMode.getGameSettings().getPlayerWhoStarts().equals(winner))
                    gameMode.getGameSettings().addWinHisGameFirstComputer();
                gameMode.getGameSettings().addWinFirstComputer();
            }
            else {
                if(gameMode.getGameSettings().getPlayerWhoStarts().equals(winner))
                    gameMode.getGameSettings().addWinHisGameSecondComputer();
                gameMode.getGameSettings().addWinSecondComputer();
            }
            gameMode.getGameSettings().addPlayedRounds();
        }
        gameMode.newGame();
    }
    private void capturePawn(int i, int j) {
        String capturedPawn = boardState[i][j];
        if (capturedPawn != null){
            StackPane cell = getCellAt(i,j);
            if (cell.getChildren().size() >1){
                cell.getChildren().remove(1);
            }
            Image capturedPawnImage;
            if (capturedPawn.equals(WHITE_PAWN)){
                capturedPawnImage=loadImage("whitePawn.png");
            }else{
                capturedPawnImage = loadImage("blackPawn.png");
            }
            ImageView capturedPawnImageView = new ImageView(capturedPawnImage);
            capturedPawnImageView.setFitWidth(40);
            capturedPawnImageView.setFitHeight(40);
            capturedPiecesDisplay.getChildren().add(capturedPawnImageView);
        }
        boardState[i][j] = null;
    }
    private StackPane getCellAt(int row, int col) {
        for (Node node : board.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);

            if (rowIndex == null) rowIndex = -1;
            if (colIndex == null) colIndex = -1;
            if (rowIndex == row && colIndex == col) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer.equals(WHITE_PAWN) ? BLACK_PAWN : WHITE_PAWN);
    }
    private void movePawn(StackPane sourceCell, StackPane destCell) {
        if (sourceCell.getChildren().size() > 1) {
            ImageView pawnImage = (ImageView) sourceCell.getChildren().get(1);
            sourceCell.getChildren().remove(pawnImage);
            destCell.getChildren().add(pawnImage);
        }
    }
    private boolean hasValidMoves(String player) {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                if (boardState[i][j] != null && boardState[i][j].equals(player)) {
                    // Check all potential moves for this pawn
                    for (int y = i - 1; y <= i + 1; y++) {
                        for (int x = j - 1; x <= j + 1; x++) {
                            // Ensure move is within bounds
                            if (y >= 0 && y < BOARD_HEIGHT && x >= 0 && x < boardState[0].length) {
                                if (isValidMove(i, j, y, x)) {
                                    return true; // Found a valid move!
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean isValidMove(int startY, int startX, int endY, int endX) {
        if (startX == endX && startY == endY) return false; // Not a move.

        String startPawn = boardState[startY][startX];
        String endPawn = boardState[endY][endX];

        if (!startPawn.equals(currentPlayer)){ return false;}

        if (startPawn.equals(WHITE_PAWN)) {
            if (endY == startY + 1) {
                if (endX == startX && endPawn == null) return true; // Forward move.
                return Math.abs(endX - startX) == 1 && BLACK_PAWN.equals(endPawn); // Diagonal move.
            }
        } else if (startPawn.equals(BLACK_PAWN)) {
            if (endY == startY - 1) {
                if (endX == startX && endPawn ==null) return true; // Forward move.
                return Math.abs(endX - startX) == 1 && WHITE_PAWN.equals(endPawn); // Diagonal move.
            }
        }
        return false;
    }
    private void handleCellClick(int i, int j, StackPane cell) {
        String currentState = boardState[i][j];
        if (currentState != null){
            selectedCell = cell;
            selectedX = j;
            selectedY = i;
        }
    }
    private void addPawnToCell(StackPane cell, Image pawnImage, double width, double height) {
        ImageView pawnImageView = new ImageView(pawnImage);
        pawnImageView.setFitWidth(width - 10);
        pawnImageView.setFitHeight(height - 10);
        cell.getChildren().add(pawnImageView);
    }
    private Image loadImage(String path) {
        return new Image(path);
    }
    public VBox createCapturedPiecesDisplay() {
        capturedPiecesDisplay = new VBox(10);
        capturedPiecesDisplay.setPadding(new Insets(10));
        Label title = new Label("Captured Pieces");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
        title.setTextFill(BORDER_COLOR);
        title.setWrapText(true);
        capturedPiecesDisplay.getChildren().add(title);
        return capturedPiecesDisplay;
    }
    public String checkWinner() {
        int whitePawnCount = 0;
        int blackPawnCount = 0;

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                if (boardState[i][j] != null) {
                    if (boardState[i][j].equals(WHITE_PAWN)) {
                        whitePawnCount++;
                        if (i == BOARD_HEIGHT - 1) {
                            return WHITE_PAWN;
                        }
                    } else if (boardState[i][j].equals(BLACK_PAWN)) {
                        blackPawnCount++;
                        if (i == 0) {
                            return BLACK_PAWN;
                        }
                    }
                }
            }
        }
        if (whitePawnCount == 0) return BLACK_PAWN;
        if (blackPawnCount == 0) return WHITE_PAWN;
        return null;
    }
    private void resetBoard(){
        System.out.println(gameMode.getGameSettings().getPlayedRounds());
        if(gameMode.getGameSettings().getMode().equals("Auto")
                && gameMode.getGameSettings().getRounds() - gameMode.getGameSettings().getPlayedRounds() == 0){
            gameMode.getGameSettings().setCanMove(false);
            board.getChildren().clear();
            Label label = new Label(gameMode.gameIsOver());
            label.setFont(Font.font("Times New Roman", FontWeight.BOLD,30));
            label.setPadding(new Insets(0,0,0,10));

            board.add(label, 0, 0);
        }
        else if(gameMode.getGameSettings().getMode().equals("Auto")){
            createBoard(this.board);
            initializedCapturedPiecesDisplay();
            gameMode.getGameSettings().setPlayerWhoStarts(currentPlayer);
            if(currentPlayer.equals("W"))
                gameMode.getGameSettings().addStartFirstComputer();
            else
                gameMode.getGameSettings().addStartSecondComputer();
            gameMode.setCurrentPlayer(currentPlayer);
            gameMode.setStateBoard(boardState);
        }
        else {
            createBoard(this.board);
            initializedCapturedPiecesDisplay();
            currentPlayer = WHITE_PAWN;
            gameMode.setCurrentPlayer(currentPlayer);
            gameMode.setStateBoard(boardState);
        }
    }
    private void initializedCapturedPiecesDisplay() {
        capturedPiecesDisplay.getChildren().clear();
        Label title = new Label("Captured Pieces");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
        title.setTextFill(BORDER_COLOR);
        title.setWrapText(true);
        capturedPiecesDisplay.getChildren().add(title);
    }
}
