package com.my.her;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.*;

public class GameMode {
    private GameSettings gameSettings;
    private final List<State> matchboxes = new ArrayList<>();
    private final List<CloneState> cloneMatchboxes = new ArrayList<>();
    private GridPane board;
    private State currentState;
    private CloneState currentStateSecond;
    private String currentPlayer;
    private final Map<State, String> take = new LinkedHashMap<>();
    private final Map<CloneState, String> takeSecond = new LinkedHashMap<>();
    private final Map<String, Label> arrows = new HashMap<>();

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public void addArrows(String bead, Label arrow) {
        this.arrows.put(bead, arrow);
    }
    public void addProbabilitiesArrow(){
        int p = (int) (100.0 / currentState.getAllBeads().size());
        for (Map.Entry<String, Label> entry : arrows.entrySet()){
            if(currentState.getAllBeads().contains(entry.getKey()))
                entry.getValue().setText(p + "%");
            else
                entry.getValue().setText("0%");
        }
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public State getCurrentState() {
        return currentState;
    }
    public CloneState getCurrentStateSecond() {
        return currentStateSecond;
    }

    public void setBoard(GridPane board) {
        this.board = board;
    }

    public void setStateBoard(String[][] boardState) {
        if(gameSettings.getMode().equals("Auto") && currentPlayer.equals("W"))
            setStateBoardSecond(boardState);
        else if(currentPlayer.equals("B"))
            setStateBoardFirst(boardState);
    }
    public void setStateBoardFirst(String[][] boardState){
        if(matchboxes.isEmpty()) {
            currentState = new State(board);
            currentState.setShowBoard(gameSettings.getMode().equals("Slow"));
            currentState.setBoardState(boardState);
            matchboxes.add(currentState);
        }
        else {
            State temp = null;
            for (State state: matchboxes){
                if(state.compareView(boardState)) temp = state;
            }
            if(temp != null){
                currentState = temp;
                currentState.refresh();
            }else {
                currentState = new State(board);
                currentState.setShowBoard(gameSettings.getMode().equals("Slow"));
                currentState.setBoardState(boardState);
                matchboxes.add(currentState);
            }
        }
    }
    public void setStateBoardSecond(String[][] boardState){
        if(matchboxes.isEmpty()) {
            currentStateSecond = new CloneState();
            currentStateSecond.setBoardState(boardState);
            cloneMatchboxes.add(currentStateSecond);
        }
        else {
            CloneState temp2 = null;
            for (CloneState state : cloneMatchboxes) {
                if (state.compareView(boardState)) temp2 = state;
            }
            if (temp2 != null) {
                currentStateSecond = temp2;
            } else {
                currentStateSecond = new CloneState();
                currentStateSecond.setBoardState(boardState);
                cloneMatchboxes.add(currentStateSecond);
            }
        }
    }
    public boolean makeStep(int x, int y, int i, int j){
        boolean step = false;
        Point2D p1 = new Point2D(x, y);
        Point2D p2 = new Point2D(i, j);
        if(gameSettings.getMode().equals("Auto") && currentPlayer.equals("W")) {
            if (currentStateSecond.takeBead(p1, p2) != null) {
                takeSecond.put(currentStateSecond, currentStateSecond.takeBead(p1, p2));
                step = true;
            }
        }
        else if(currentPlayer.equals("B")) {
            if (currentState.takeBead(p1, p2) != null) {
                take.put(currentState, currentState.takeBead(p1, p2));
                step = true;
            }
        }
        else if(!gameSettings.getMode().equals("Auto")){
            step = true;
        }
        return step;
    }
    public String gameIsOver(){
        double first = gameSettings.getWinHisGameFirstComputer() == 0 ? 0 : gameSettings.getWinHisGameFirstComputer() / (double) gameSettings.getStartFirstComputer();
        double second = gameSettings.getWinHisGameSecondComputer() == 0 ? 0 : gameSettings.getWinHisGameSecondComputer() / (double) gameSettings.getStartSecondComputer();
        return "Rounds Played: " + gameSettings.getRounds() +
                "\n\nHER with white figure Wins: " + gameSettings.getWinFirstComputer() +
                "\nRounds Where HER Went First: " + gameSettings.getStartFirstComputer() +
                "\nHER Wins When Going First: " + gameSettings.getWinHisGameFirstComputer() +
                "\nRatio of Wins When Going First: %.1f%%\n".formatted(first) +
                "\n\nHER with black figure Wins: " + gameSettings.getWinSecondComputer() +
                "\nRounds Where HER Went First: " + gameSettings.getStartSecondComputer() +
                "\nHER Wins When Going First: " + gameSettings.getWinHisGameSecondComputer() +
                "\nRatio of Wins When Going First: %.1f%%\n".formatted(second);
    }
    public void newGame(){
        take.clear();
        takeSecond.clear();
        for (Map.Entry<String, Label> entry : arrows.entrySet()){
            entry.getValue().setText("0%");
        }
        if(gameSettings.getMode().equals("Auto") && currentPlayer.equals("W")){
            Map.Entry<CloneState, String> last2 = null;
            for (Map.Entry<CloneState, String> entry : takeSecond.entrySet()) {
                last2 = entry;
            }
            if (last2 != null)
                last2.getKey().removeBead(last2.getValue());
        }else if(currentPlayer.equals("B")){
            Map.Entry<State, String> last = null;
            for (Map.Entry<State, String> entry : take.entrySet()){
                last = entry;
            }
            if(last != null)
                last.getKey().removeBead(last.getValue());
        }
    }
}
