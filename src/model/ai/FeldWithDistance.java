package model.ai;

import model.game.Feld;

public class FeldWithDistance implements Comparable{

    private double distance;
    private Feld feld;

    public FeldWithDistance(Feld feld, double distance){
        this.feld = feld;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return feld.getColumn() + "|" + feld.getRow() + "| -> " +
                feld.getToken().name()+ ", distance = " + distance;
    }

    public Feld getFeld(){
        return this.feld;
    }

    public int compareTo(Object otherFeld){
        if(otherFeld instanceof FeldWithDistance){
            return Double.compare(distance,((FeldWithDistance) otherFeld).getDistance());
        }
        throw new ClassCastException("provided Object has wrong type");

    }

    public double getDistance(){
        return this.distance;
    }
}
