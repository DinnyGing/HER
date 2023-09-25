package com.my.her;

public class GameSettings {
    private final String mode;
    private boolean chooseButton;
    private boolean canMove;
    private int speed;
    private String playerWhoStarts;
    private int winFirstComputer;
    private int winSecondComputer;
    private int winHisGameFirstComputer;
    private int winHisGameSecondComputer;

    private int startFirstComputer;
    private int startSecondComputer;
    private int rounds;
    private int playedRounds;

    public GameSettings(String mode) {
        this.mode = mode;
        winFirstComputer = 0;
        winSecondComputer = 0;
        switch (mode) {
            case "Slow" -> {
                chooseButton = true;
                canMove = false;
                speed = 1000;
            }
            case "Fast" -> {
                chooseButton = false;
                canMove = true;
                speed = 150;
            }
            case "Auto" -> {
                chooseButton = false;
                canMove = false;
                speed = 150;
                startFirstComputer = 1;
                playerWhoStarts = "W";
            }
        }
    }

    public String getMode() {
        return mode;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isChooseButton() {
        return chooseButton;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getWinFirstComputer() {
        return winFirstComputer;
    }

    public void addWinFirstComputer() {
        this.winFirstComputer++;
    }

    public int getWinSecondComputer() {
        return winSecondComputer;
    }

    public void addWinSecondComputer() {
        this.winSecondComputer++;
    }

    public int getWinHisGameFirstComputer() {
        return winHisGameFirstComputer;
    }

    public void addWinHisGameFirstComputer() {
        this.winHisGameFirstComputer++;
    }

    public int getWinHisGameSecondComputer() {
        return winHisGameSecondComputer;
    }

    public void addWinHisGameSecondComputer() {
        this.winHisGameSecondComputer++;
    }

    public String getPlayerWhoStarts() {
        return playerWhoStarts;
    }

    public void setPlayerWhoStarts(String playerWhoStarts) {
        this.playerWhoStarts = playerWhoStarts;
    }

    public int getStartFirstComputer() {
        return startFirstComputer;
    }

    public void addStartFirstComputer() {
        this.startFirstComputer++;
    }

    public int getStartSecondComputer() {
        return startSecondComputer;
    }

    public void addStartSecondComputer() {
        this.startSecondComputer++;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getPlayedRounds() {
        return playedRounds;
    }

    public void addPlayedRounds() {
        this.playedRounds++;
    }
}
