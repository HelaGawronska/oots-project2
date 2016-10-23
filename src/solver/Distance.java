package solver;

/**
 * Created by hela on 9/21/16.
 */
public class Distance implements Comparable<Distance>{
    private int id;
    private double distance;

    public Distance(int index, double dist){
        this.id = index;
        this.distance = dist;
    }


    // make distances comparable by id - returns 0 if id are the same
    public int compareTo(Distance d) {
        return -Double.compare(id, d.id);
    }

    public int getId(){ return id;}

    public double getDistance(){ return distance;}
}
