package solver;

/**
 * Created by hela on 9/23/16.
 */
public class Costs {
    private double costOfDistance;
    private double costOfHour;

    public Costs(double dist, double hour){
        this.costOfDistance = dist;
        this.costOfHour = hour;
    }

    public double getCostOfDistance(){
        return this.costOfDistance;
    }

    public double getCostOfHour(){
        return this.costOfHour;
    }
}
