package solver;

import instance.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import solution.Route;

public class NaiveSolver implements Solver {
    public ArrayList<Request> allRequests = new ArrayList<>();
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public boolean ifFirst = true;
    public int deniedRequests = 0;


    @Override
    public double solve(ArrayList<Route> solution,
                        ArrayList<Request> unplannedRequests, double time, ObjectiveFunction cf) {


        removeDepot(solution);
//         Insert each unplanned visit after turn starting with the last in the list
        for (int i = unplannedRequests.size() - 1; i >= 0; i--) {
            Request r = unplannedRequests.remove(i);

            boolean inserted = insert(r, solution, time, cf);

            if (!inserted) {
                Route ro = new Route(time);
                solution.add(ro);
                boolean inserted2 = insert(r, solution, time, cf);
                if (!inserted2) {
                    System.err.println("\nFailed to insert request at time " + time);
                    System.err.println(solution.get(solution.size() - 1));
                    System.err.println(r);
                    System.err.println(solution.get(solution.size() - 1).getVisit(0).getDistance(r.getPickup()) / Route.getSpeed());
                    System.err.println(r.getPickup().getDistance(r.getDelivery()) / Route.getSpeed());
                    System.exit(1);
                }

            }

        }
        ////////////////////////////// 19.10 ///////////////////////////////////////////////////
        MyObjectiveFunction costF = new MyObjectiveFunction();

//        for (Request request: unplannedRequests) {
//            insertionBestFound(solution, request, costF, time);
//        }


        int problems = 0;
        for (Route r: solution){
            if (!r.feasibilityCheck()) {
                problems++;
            }
        }
        System.err.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    number of problems: " +problems );


        ifFirst = false;

        insertDepot(solution);

        // Request next solver to be in an infinite amount of time OR when next event occurs
        return Double.POSITIVE_INFINITY;
    }

    public boolean insert(Request r, ArrayList<Route> solution, double time, ObjectiveFunction cf) {

        removeDepot(solution);
        Route lastRoute = solution.get(solution.size() - 1);

        lastRoute.AddVisit(lastRoute.size(), r.getPickup(), time);
        lastRoute.AddVisit(lastRoute.size(), r.getDelivery(), time);

        // Success when inserting?
        if (lastRoute.getArrivalTime(lastRoute.size() - 2) > r.getPickup().getTwEnd() ||
                lastRoute.getArrivalTime(lastRoute.size() - 1) > r.getDelivery().getTwEnd()) {
            lastRoute.removeVisit(lastRoute.size() - 1, time);
            lastRoute.removeVisit(lastRoute.size() - 1, time);
            return false;
        }

        return true;
    }


    // If a name for the solver is ever required, return this:
    public String toString() {
        return "NaiveSolver";
    }

    @Override
    public void handleCancellation(ArrayList<Route> solution, ArrayList<Request> revealedVisits, double time,
                                   ObjectiveFunction cf, Request cancelled) {

        removeDepot(solution);
        Visit v = cancelled.getPickup();

        for (Route r: solution){
            if (r.containsThisVisit(v)) {
                int index = r.findIndex(v);
                Visit correspVisit = r.findCorrespondingVisit(v);
                int correspIndex = r.findIndex(correspVisit);

                if (!r.isLocked(index)) {
                    // check only if pick up is locked (I assume that cancellation arrives before someone is pick up so delivery cannot be locked)
                    r.removeVisit(correspIndex, time); // removing delivery
                    r.removeVisit(index, time); // removing pick up
                    break;
                } else {
                    //if pick up visit is locked
                    // set demand on 0, change place of delivery on depot, change time windows
                    r.getVisit(index).getRequest().changeDemand(0);
                    r.getVisit(correspIndex).changeLocalisation(0,0);
                    r.getVisit(correspIndex).changeTW(0,Double.POSITIVE_INFINITY);
                    // set is as Fake visit since cancelled
                    r.getVisit(correspIndex).setIsFake(true);
                    r.getVisit(index).setIsFake(true);
                    // change place in a route - put it just before going back to the depot
                    Visit toBeRemoved = r.removeVisit(correspIndex , time);
                    if (r.getVisit(r.size()-1).getVisitType() == VisitType.Depot && !r.isLocked(r.size()-1)) {
                        // if depot is at the end and is not locked  - insert before
                        r.AddVisit(r.size() - 1, toBeRemoved, time);
                    } else {
                        r.AddVisit(r.size(), toBeRemoved, time);
                    }
                    break;
                }
            } else if (r == solution.get(solution.size()-1)){ // "cancelled" event was not scheduled jet...
                System.out.println("!! BABOL !!!    visit type: " + v.getVisitType() + " request id: " +v.getRequest().getId());
                //System.out.println(solution);
            }

        }

    }

    ////////////////////////////////////////////////////////////////////////

    //method for inserting a depot with time window given
    public void insertDepot(ArrayList<Route> solution) {
        for (Route r : solution) {
            if (r.getVisit(r.size() - 1).getVisitType() != VisitType.Depot ) {
//                Visit fakeDepot = new Visit(0, 0);
//                r.AddVisit(r.size(), fakeDepot, 1);
                r.AddVisit(r.size(), r.getVisit(0), 100000000.0);
            }
        }
    }

    public void removeDepot(ArrayList<Route> solution){
        for(Route r : solution){
            if(r.getVisit(r.size()-1).getX()==0 && r.getVisit(r.size()-1).getY()==0 && !r.isLocked(r.size()-1) && r.getVisit(r.size()-1).getVisitType() == VisitType.Depot){
                r.removeVisit(r.size()-1, 10000);
            }
        }
    }

    // first improvement
    public void insertionFI(ArrayList<Route> solution, Request request, MyObjectiveFunction cf, double time) {
        double initialCost;
        double newCost;
        boolean betterFound = false;

        Route lastR = solution.get(solution.size()-1);

        lastR.AddVisit(lastR.size() - 2, request.getPickup(), time);
        lastR.AddVisit(lastR.size() - 2, request.getDelivery(), time);
        initialCost = cf.getSingleRouteCost(lastR);

        lastR.removeVisit(lastR.size() - 2, time);
        lastR.removeVisit(lastR.size() - 2, time);

        for (Route route : solution) {
            for (int p = 0; p < route.size(); p++) {
                route.AddVisit(p, request.getPickup(), time);
                for (int d = p + 1; d < route.size(); d++) {
                    route.AddVisit(d, request.getDelivery(), time);
                    newCost = cf.getSingleRouteCost(route);

                    if (newCost < initialCost) {
                        betterFound = true;
                        break;
                    } else {
                        route.removeVisit(d, time);
                    }

                }
                if(betterFound) {
                    break;
                } else {
                    route.removeVisit(p, time);
                }
            }
            if (betterFound) break;
        }

    }

    /////////////////

    public BestInsert bestInsertInRoute(ArrayList<Route> solution, Route route, int routeIndex, Request request, ObjectiveFunction cf, double time){
        double initialCost = Double.MAX_VALUE;
        double newCost;
        BestInsert best = null;
        boolean inserted = false;

//        removeDepot(solution);

        // try to insert simply at the end of the route
        route.AddVisit(route.size(), request.getPickup(), time);
        route.AddVisit(route.size(), request.getDelivery(), time);

        if (route.feasibilityCheck()) {
            initialCost = ((MyObjectiveFunction) cf).getSingleRouteCost(route);
            best = new BestInsert(routeIndex, route.size() - 2, route.size() - 1, initialCost);
            // // TODO: 10/18/16 check if object best is changing
        }

        route.removeVisit(route.size() - 1, time);
        route.removeVisit(route.size() - 1, time);
//        route.removeVisit(route.findIndex(request.getPickup()), time);
//        route.removeVisit(route.findIndex(request.getDelivery()), time);

        for (int p = 0; p <= route.size()-1; p++) {
            if (route.isLocked(p)) continue;
            route.AddVisit(p, request.getPickup(), time); // TODO: 10/18/16 missing feasibility check
            if (!route.checkTW()) {
                route.removeVisit(p, time);
                continue;
            }
            for (int d = p + 1; d <= route.size()-1; d++) {
                if (route.isLocked(d)) continue;
                route.AddVisit(d, request.getDelivery(), time);
                if (!route.feasibilityCheck()) {
                    route.removeVisit(d, time);
                    continue;
                }
                newCost = ((MyObjectiveFunction) cf).getSingleRouteCost(route);
                if (newCost < initialCost) {
                    best = new BestInsert(routeIndex, p, d, newCost);
                    initialCost = newCost;
                }
                route.removeVisit(d, time);
//                route.removeVisit(route.findIndex(request.getDelivery()), time);
            }
            route.removeVisit(p, time);
//            route.removeVisit(route.findIndex(request.getPickup()), time);
        }
        // each possible insertion was checked and no feasible found
        // for static request - create a new route
        // for dynamic - refuse request
        if (best == null){
            if (time == -1) {
                Route newRoute = new Route(time);
                solution.add(newRoute);
                routeIndex = solution.size()-1;
                System.out.println("-----------> new truck");
                best = bestInsertInRoute(solution, newRoute, routeIndex, request, cf, time);
            } else {
                System.out.println("-----------> REFUSED REQUEST");
                deniedRequests++;

            }


        }

        return best;
    }

    public void insertionBestFound(ArrayList<Route> solution, Request request, ObjectiveFunction cf, double time) {
        ArrayList<BestInsert> bestInsertArrayList = new ArrayList<>();
        int routeId;
        int pickId;
        int deliveryId;
        removeDepot(solution);

        for (int r = 0; r < solution.size(); r++) {
            if (solution.get(r).feasibilityCheck())
            bestInsertArrayList.add(bestInsertInRoute(solution, solution.get(r), r, request, cf, time ));
        }
        // sort best insertions with respect to cost of insertion (decreasing order?)
        Collections.sort(bestInsertArrayList);

        for (int i = bestInsertArrayList.size()-1; i >= 0; i--) {
            routeId = bestInsertArrayList.get(i).getRouteIndex();
            pickId = bestInsertArrayList.get(i).getBestIndexPickUp();
            deliveryId = bestInsertArrayList.get(i).getBestIndexDelivery();

            solution.get(routeId).AddVisit(pickId, request.getPickup(), time);
            solution.get(routeId).AddVisit(deliveryId, request.getDelivery(), time);

            if (!solution.get(routeId).feasibilityCheck()) {
                solution.get(routeId).removeVisit(deliveryId, time);
                solution.get(routeId).removeVisit(pickId, time);
                System.err.println("\n[insertionBestFound] infeasibility occurred\n");
                break;
            }
        }

    }


//    // check feasibility - if infeasible create a new route
//    if (!route.feasibilityCheck()) {
//        System.out.println("00000000000000000000000000000000000000000000");
//        Route newRoute = new Route(time);
//        route = newRoute;
//        solution.add(route);
//        routeIndex = solution.size()-1;
//    } // FIXME: 10/19/16 CHECK IT !!!!!!!!!!!!!!!!!!!!!!!


}

