package instance;

import java.text.DecimalFormat;

public class Visit implements Comparable<Visit>{
	private double x;
	private double y;
	private double twStart=0;
	private double twEnd=100000000;
	private static final DecimalFormat myFormatter = new DecimalFormat("###.000");
	private VisitType type;
	private int requestId = -1;
	private Request r;

    public double pickUpTime;
    private boolean isFake = false; // NEW 19.10 // isFake is used to avoid evaluating maxRideTime of cancelled visits
	
	public Visit(double x, double y){
		this.x = x;
		this.y = y;
		type = VisitType.Depot; 
	}
	
	public Visit(double x, double y, double twStart, double twEnd){
		this.x = x;
		this.y = y;
		this.twStart = twStart;
		this.twEnd = twEnd;
		type = VisitType.Depot;
	}
	
	
	
	/**
	 * Set the id of the corresponding request
	 * @param id
	 */
	public void setRequestId(int id){
		requestId = id;
	}
	
	/**
	 * 
	 * @return The id of the associated request
	 */
	public int getRequestId(){
		return requestId;
	}
	
	/**
	 * Set the type of the visit
	 * @param t
	 */
	public void setVisitType(VisitType t){
		type = t;
	}
	
	/**
	 * 
	 * @return The type of the visit
	 */
	public VisitType getVisitType(){
		return type;
	}
	
	/**
	 * Calculates the Euclidian distance between two visits
	 * @param v
	 * @return
	 */
	public double getDistance(Visit v){
		return Math.sqrt( (v.x-x)*(v.x-x)+(v.y-y)*(v.y-y) );
	}
	
	/**
	 * 
	 * @return The x coordinate of the visit
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * 
	 * @return The y coordinate of the visit
	 */
	public double getY(){
		return y;
	}

	/**
	 * 
	 * @return The start of the time window
	 */
	public double getTwStart(){
		return twStart;
	}
	
	/**
	 * 
	 * @return The end of the time window
	 */
	public double getTwEnd(){
		return twEnd;
	}
	
	/**
	 * Returns a string representation of the visit
	 */
	public String toString(){
		if(twEnd>-1 && twEnd<Double.MAX_VALUE)
			return "<Visit " +this.getVisitType() +" ("+myFormatter.format(x)+","+myFormatter.format(y)+") tw:["+myFormatter.format(twStart)+","+myFormatter.format(twEnd)+"]>";
		return "<Visit " +this.getVisitType() +" ("+myFormatter.format(x)+","+myFormatter.format(y)+")>";
	}
	
	public String getTwLabel(){
        if (twEnd<=1000){
            return ("["+myFormatter.format(twStart)+", "+myFormatter.format(twEnd)+"]");}
        else{
            return ("["+myFormatter.format(twStart)+", 1000]");}
//		return "["+myFormatter.format(twStart)+", "+myFormatter.format(twEnd)+"]";
	}

	/**
	 * Get the request the visit is a part of
	 * @return
	 */
	public Request getRequest() {
		return r;
	}

	/**
	 * Set the request the visit is a part of
	 * @param r
	 */
	public void setRequest(Request r) {
		this.r = r;
	}



	///////////////////// NEW STUFFF ////////////////////////////////

    // method computes a pick up time for delivery visit (time which customer has to assume to be pick up)
//    public void computePickUpTime() {
//        if(this.type == VisitType.Delivery) {
//            double distance = this.getDistance(this.getRequest().getPickup());
//            double directRideTime = distance / 50;
//            double pickUpTime = this.getTwEnd() - Math.max(0.30, directRideTime); // tw end?
//            this.pickUpTime = pickUpTime;
//        }
//    }

    public void setPickUpTime(double t) {
        this.pickUpTime = t;
    }

    public double getPickUpTime() {
        return pickUpTime;
    }

    // NEW: comparator - to compere visits with respect to pick up times
    // if returns -1 - this object is more important
    // returns -1 if this visit's twStart/PickUpTime is before twStart/PickUpTime of input visit v
    // or if input visit is a Depot
    // returns 1 if this visit's twStart/PickUpTime is after twStart/PickUpTime of input visit v
    // returns 0 if twStart/PickUpTime are the same
    //
    public int compareTo(Visit v) {
		this.getRequest().computePickUpTime();
        v.getRequest().computePickUpTime();
        if(this.type == VisitType.Pickup && v.type == VisitType.Pickup){
            return this.getTwStart() < v.getTwStart() ?-1: this.getTwStart() > v.getTwStart() ?1:0; // tw start?
        } else if(this.type == VisitType.Pickup && v.type == VisitType.Delivery) {
//            v.computePickUpTime();
            return this.getTwStart() < v.getPickUpTime() ?-1: this.getTwStart() > v.getPickUpTime() ?1:0; // tw start?
        } else if(this.type == VisitType.Delivery && v.type == VisitType.Pickup) {
//            this.computePickUpTime();
            return this.getPickUpTime() < v.getTwStart() ?-1: this.getPickUpTime() > v.getTwStart() ?1:0; // tw start?
        } else if( this.type == VisitType.Delivery && v.type == VisitType.Delivery ) {
//            v.computePickUpTime();
//            this.computePickUpTime();
            return this.getPickUpTime() < v.getPickUpTime() ?-1: this.getPickUpTime() > v.getPickUpTime() ?1:0;
        } else if (this.type == VisitType.Depot){ // WHAT IF ONE OF THEM IS DEPOT?
            return 1;
        } else if (v.type == VisitType.Depot) {
            return -1; // I do not know, I'm too tired...............
        } else {
            return 0;
        }


    }

	//////////////// 18.10 ////////////////////////////////////////////
    public void changeLocalisation(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public void changeTW(double newXTWstart, double newTWend) {
        this.twStart = newXTWstart;
        this.twEnd = newTWend;
    }


    //////////////////// 19.10 ///////////////////////////////////

    // if an event is a cancellation, in handleCancellation after moving visit or whatever isFake is set on true
    // by default it is false
    // isFake is used to avoid evaluating maxRideTime of cancelled visits
    public void setIsFake(boolean t) {
        this.isFake = t;
    }

    public boolean getIsFake() {
        return this.isFake;
    }



}
