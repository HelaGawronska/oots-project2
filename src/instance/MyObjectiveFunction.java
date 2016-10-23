package instance;

import java.util.ArrayList;

import solution.Route;

public class MyObjectiveFunction implements ObjectiveFunction {
    public double w1 = 0.25;
    public static int leasingCostFactor = 1000;
    public double w2 = 0.25;
    public static int hourlyCostFactor = 100; // no idea about adequate value...
    public double w3 = 0.25;
    public static double fuelCostFactor = 0.8224; // assume that a car combusts in average 8l per 100km -> 0.8224kr per 1km
    public double w4 = 0.25;
    public static int deniedCostFactor = 1000;

    public double cost;

    public void checkWeights() {
        if (w1 + w2 + w3 + w4 != 1) {
            System.err.print("input weights do not sum up to 1 ");
        }
    }


    public MyObjectiveFunction() {

    }

    public double getDistanceCost(ArrayList<Route> solution) {
        double distance = 0;
        for (Route r : solution) {
            distance = +r.getDistance();
        }
        return distance*fuelCostFactor;
    }

    public double getHourlyCost(ArrayList<Route> solution) {
        double time = 0;
        for (Route r : solution) {
            time = +r.getTime()*hourlyCostFactor;
        }
        return time;
    }


    public double getPulloutCost(ArrayList<Route> solution) {
        return solution.size()*leasingCostFactor;
    }

    public double getDeniedCost(ArrayList<Route> solution) {
        int instanceSize = 0;
        int servedVisit = 0;
        for(Route r: solution) {
            servedVisit = +r.size()/2 -1; // subtract depot
        }
        return 0;

    }


    public double getSingleRouteCost(Route route){ // FIXME: 10/17/16 denied costs are missing
        return route.getDistance()*fuelCostFactor + leasingCostFactor + route.getTime()*hourlyCostFactor;
    }

    @Override
    public double getCost(ArrayList<Route> solution) { // FIXME: 10/17/16 denied costs are missing - need to store somehow the number of denied visits
        double cost = 0;
        cost += getPulloutCost(solution);
        cost += getHourlyCost(solution);
        cost += getDistanceCost(solution);
//        cost += getDeniedCost(solution);
        return cost;
    }
}
