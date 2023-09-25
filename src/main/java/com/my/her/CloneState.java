package com.my.her;

import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

public class CloneState {
    String[][] boardState;
    private Matchbox matchbox;
    private final String[] color = new String[]{"Green", "Red", "Blue", "Yellow"};
    Map<String, Point2D[]> pair = new HashMap<>();

    public void setBoardState(String[][] boardState) {
        copyView(boardState);
        matchbox = new Matchbox();
        fillView();
    }
    public String takeBead(Point2D p1, Point2D p2){
        for (Map.Entry<String, Point2D[]> entry: pair.entrySet()){
            if(entry.getValue()[0].getX() == p1.getX() && entry.getValue()[0].getY() == p1.getY()
            && entry.getValue()[1].getX() == p2.getX() && entry.getValue()[1].getY() == p2.getY()){
                return entry.getKey();
            }
        }
        return null;
    }
    public String randomBead(){
        return matchbox.pickMove();
    }
    public void removeBead(String bead){
        matchbox.removeBead(bead);
        pair.remove(bead);
    }
    public boolean compareView(String[][] boardState){
        boolean same = true;
        for (int i = 0; i < this.boardState.length; i++){
            for (int j = 0; j < this.boardState[i].length; j++){
                if(this.boardState[i][j] != null && boardState[i][j] != null
                        && !this.boardState[i][j].equals(boardState[i][j]))
                    same = false;
                else if(this.boardState[i][j] != null && boardState[i][j] == null)
                    same = false;
                else if(this.boardState[i][j] == null && boardState[i][j] != null)
                    same = false;
            }
        }
        return same;
    }
    public void copyView(String[][] boardState){
        this.boardState = new String[boardState.length][boardState[0].length];
        for (int i = 0; i < this.boardState.length; i++){
            System.arraycopy(boardState[i], 0, this.boardState[i], 0, this.boardState[i].length);
        }
    }
    private void fillView() {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] != null && boardState[i][j].equals("W")) {
                    for (int y = i - 1; y <= i + 1; y++) {
                        for (int x = j - 1; x <= j + 1; x++) {
                            if (y >= 0 && y < 3 && x >= 0 && x < boardState[0].length) {
                                if (isValidMove(i, j, y, x)) {
                                    String bean = color[count];
                                    matchbox.addBead(bean);
                                    pair.put(bean, new Point2D[]{new Point2D(i, j), new Point2D(y, x)});
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private boolean isValidMove(int startY, int startX, int endY, int endX) {
        if (startX == endX && startY == endY) return false; // Not a move.
        String endPawn = boardState[endY][endX];
        if (endY == startY + 1) {
            if (endX == startX && endPawn == null) return true; // Forward move.
            return Math.abs(endX - startX) == 1 && "B".equals(endPawn); // Diagonal move.
        }
        return false;
    }
}
