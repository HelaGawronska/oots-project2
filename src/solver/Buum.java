package solver;

import instance.Cluster;
import instance.ObjectiveFunction;
import instance.Request;
import instance.Visit;
import solution.Route;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hela on 10/11/16.
 */
public class Buum implements Solver{



//    public ArrayList<Cluster> divideOnClusters(ArrayList<Request> requestsList) {
//        Request.sortRequestsByTime(requestsList);
//
//    }


    @Override
    public double solve(ArrayList<Route> solution,
                        ArrayList<Request> unplannedRequests, double time, ObjectiveFunction cf) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public void handleCancellation(ArrayList<Route> solution, ArrayList<Request> unplannedVisits, double time,
                                   ObjectiveFunction cf, Request cancelled) {

        //TODO handle it:)
    }
}
