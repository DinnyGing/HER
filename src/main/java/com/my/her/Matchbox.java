package com.my.her;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matchbox {
    private final List<String> beads;

    public Matchbox() {
        beads = new ArrayList<>();
    }

    public void addBead(String move) {
        beads.add(move);
    }

    public String pickMove() {
        if (beads.isEmpty()) return null;
        Random random = new Random();
        return beads.get(random.nextInt(beads.size()));
    }

    public void removeBead(String move) {
        beads.remove(move);
    }

    public boolean isEmpty() {
        return beads.isEmpty();
    }

    public List<String> getBeads() {
        return beads;
    }
}
