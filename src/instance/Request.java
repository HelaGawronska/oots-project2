package instance;

import java.util.ArrayList;
import java.util.Collections;

public class Request implements Comparable<Request> {
    private Visit pickup;
    private Visit delivery;
    private int demand;

    private int id;
    private static int maxId = 0;

    public Request(Visit pu, Visit dl, int demand) {
        pickup = pu;
        delivery = dl;

        id = maxId;
        maxId++;

        pickup.setVisitType(VisitType.Pickup);
        delivery.setVisitType(VisitType.Delivery);
        pickup.setRequestId(id);
        delivery.setRequestId(id);
        pickup.setRequest(this);
        delivery.setRequest(this);

        this.demand = demand;
    }

    public Visit getPickup() {
        return pickup;
    }


    public Visit getDelivery() {
        return delivery;
    }


    public int getDemand() {
        return demand;
    }

    public int getId() {return id;}


    @Override
    public String toString() {
        return "<Request: " + id + " " + pickup + " -> " + delivery + " demand " + demand + ">";
    }


    ///////////// NEW STUFFF //////////////////

    // NEW: comparator - to compere requests with respect to distance between pick up and delivery
    // returns -1 if this request has shorter distance than input request r
    // returns 1 if this request has grater distance than input request r
    // returns 0 if distances are equal
    public int compareTo(Request r) {
        return this.getPickup().getDistance(this.getDelivery()) < r.getPickup().getDistance(r.getDelivery()) ? -1 :
                this.getPickup().getDistance(this.getDelivery()) > r.getPickup().getDistance(r.getDelivery()) ? 1 : 0; //:doSecodaryOrderSort(o);
    }

    public void computePickUpTime() {
        if (this.getPickup().getTwStart() == 0.0) {
            Visit v = this.getPickup();
            double distance = v.getDistance(this.getDelivery());
            double directRideTime = distance / 50;
            double pickUpTime = this.getDelivery().getTwEnd() - Math.max(0.30, directRideTime); // tw end?
            v.pickUpTime = pickUpTime;
        }
    }

    // decreasing order
    public static void sortRequestsByTime(ArrayList<Request> requestsList) {
        ArrayList<Request> rList = new ArrayList<>(requestsList);
        ArrayList<Visit> pickUpVisitList = new ArrayList<>();
        for (Request r : rList) {
            pickUpVisitList.add(r.getPickup());
        }
        Collections.sort(pickUpVisitList);
        Visit v;
        for (int i = 0; i < pickUpVisitList.size(); i++) {
            v = pickUpVisitList.get(i);
            requestsList.set(i, v.getRequest());
        }
    }

    ///////////////////////////// 18.10 //////////////////////////////
    public void changeDemand(int newDemand) {
        this.demand = newDemand;
    }


}
