package solver;

import java.util.ArrayList;

/**
 * Created by hela on 9/23/16.
 */
public class BestInsert implements Comparable<BestInsert> {
    private int routeIndex;
    private int bestIndexPickUp; // best index to place a new visit in a route of index routeIndex
    private int bestIndexDelivery;
    private double costOfInsertion;

    public BestInsert(int r, int p, int b, double c){
        this.routeIndex = r;
        this.bestIndexPickUp = p;
        this.bestIndexDelivery = b;
        this.costOfInsertion = c;
    }

    public BestInsert(int r, int p, int b){
        this.routeIndex = r;
        this.bestIndexPickUp = p;
        this.bestIndexDelivery = b;
    }

    public int getRouteIndex(){
        return this.routeIndex;
    }

    public double getCostOfInsertion(){
        return this.costOfInsertion;
    }


    public int getBestIndexPickUp() {
        return bestIndexPickUp;
    }

    public int getBestIndexDelivery() {
        return bestIndexDelivery;
    }

    ///////////// NEW STUFFF //////////////////

    // NEW: comparator - to compere BestInsert with respect to costOfInsertion
    // returns -1 if this BestInsert has lower cost than input BestInsert b
    // returns 1 if this BestInsert has greater cost than input BestInsert b
    // returns 0 if costs are equal
    public int compareTo(BestInsert b) {
        return this.getCostOfInsertion() < b.getCostOfInsertion() ? -1: this.getCostOfInsertion() > b.getCostOfInsertion() ? 1:0;
    }
}
