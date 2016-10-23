package instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hela on 10/11/16.
 */
public class Cluster {
    public ArrayList<Request> requestList = new ArrayList<>();
    public static int maxrId = 0;
    public int clusterId;
    public double twStart;
    public double twEnd;


    public Cluster(ArrayList<Request> requests) {

        this.clusterId = maxrId;
        maxrId++;
        this.requestList = new ArrayList<Request>(requests);
    }

    public Cluster(ArrayList<Request> requests, double twS, double twE) {
        this.clusterId = maxrId;
        maxrId++;
        this.twStart = twS;
        this.twEnd = twE;
        this.requestList = new ArrayList<Request>(requests);
    }


    public void sortByDistance() {
        Collections.sort(this.requestList);
    }


    public void setTwStart(double twS) {
        this.twStart = twS;
    }

    public void setTwEnd(double twE) {
        this.twEnd = twE;
    }

    public double getTwStart() {
        return this.twStart;
    }

    public double getTwEnd() {
        return this.twEnd;
    }

    public int getClusterId() {
        return this.clusterId;
    }

    public ArrayList<Request> getRequestList() {
        return this.requestList;
    }

}
