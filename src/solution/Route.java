package solution;

import instance.Request;
import instance.Visit;
import instance.VisitType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Route {
	private ArrayList<Visit> visits = new ArrayList<Visit>();
	private ArrayList<Double> arrivalTimes = new ArrayList<Double>();
	private ArrayList<Double> departureTimes = new ArrayList<Double>();	
	private ArrayList<Boolean> locked = new ArrayList<Boolean>();
	private static double speed = 50;
	private static final DecimalFormat myFormatter = new DecimalFormat("###.000");
	
	// New bounds
	private ArrayList<Double> lbArr = new ArrayList<Double>();
	private ArrayList<Double> lbDep = new ArrayList<Double>();
	private ArrayList<Double> ubDep = new ArrayList<Double>();
	private boolean timeFeasible = true;
	
	//TODO You may update this class to accommodate the specificities of DARP
	
	public Route(double time){
		// Add a visit representing start in orego
		AddVisit(0, new Visit(0, 0),0);
		arrivalTimes.set(0, time);
		departureTimes.set(0, -1.0);
		locked.set(0,true);
	}
	
	/**
	 * A constructor creating a copy of a route.
	 * @param r The rout to be copied
	 */
	public Route(Route r){
		visits.addAll(r.visits);
		arrivalTimes.addAll(r.arrivalTimes);
		departureTimes.addAll(r.departureTimes);
		locked.addAll(r.locked);
		lbArr.addAll(r.lbArr);
		lbDep.addAll(r.lbDep);
		ubDep.addAll(r.ubDep);
		speed = r.speed;
	}
	
	/**
	 * Remove a visit from the route. Fails if the visit is locked already.
	 * @param index The index of the visit to remove
	 * @param time The current time to be used when updating timings
	 * @return The visit that was removed
	 * @throws RuntimeException If the visit is locked or if index is out of bounds.
	 */
	public Visit removeVisit(int index, double time) throws RuntimeException {
		if(index>=visits.size())
			throw new RuntimeException("Attempted to remove index "+index+" of a route of size "+visits.size());
		if(locked.get(index)){
			System.out.println("Removeing "+index+" at time "+time);
			System.out.println(this);
			throw new RuntimeException("Attempted to remove a locked visit");
		}
		locked.remove(index);
		arrivalTimes.remove(index);
		departureTimes.remove(index);
		Visit removed = visits.remove(index);
		updateTime(time);
		return removed;
	}
	
	/**
	 * 
	 * @param index
	 * @return The visit at the specified index
	 */
	public Visit getVisit(int index){
		return visits.get(index);
	}
	
	/**
	 * Return the arrival time of the index'th visit 
	 * @param index
	 * @return
	 */
	public double getArrivalTime(int index){
		return arrivalTimes.get(index);
	}
	
	/**
	 * 
	 * @return A copy of the arrival times
	 */
	public ArrayList<Double> getArrivalTimes(){
		return new ArrayList<Double>(arrivalTimes);
	}

	/**
	 * Return the departure time of the index'th visit 
	 * @param index
	 * @return
	 */
	public double getDepartureTime(int index){
		return departureTimes.get(index);
	}
	
	/**
	 * 
	 * @return A copy of the departure times
	 */
	public ArrayList<Double> getDepartureTimes(){
		return new ArrayList<Double>(departureTimes);
	}
	
	/**
	 * Function to determine is a visit is locked
	 * @param index The index to check for locked status
	 * @return True is and only if the visit at the specified index is locked.
	 */
	public boolean isLocked(int index){
		if(index==visits.size())
			return false;
		return locked.get(index);
	}
	
	/**
	 * 
	 * @return A list of boolean indicating if a visit is locked
	 */
	public ArrayList<Boolean> getIsLocked(){
		return new ArrayList<Boolean>(locked);
	}
	
	/**
	 * 
	 * @return The size of the route including the depot in the start
	 */
	public int size(){
		return visits.size();
	}
	
	/**
	 * Adds a visit at the specified index and updates timings accordingly
	 * @param index The index to insert the visit at
	 * @param v The visit to insert
	 * @param time The time the insertion is done, to be used when updating timings
	 * @throws RuntimeException
	 */
	public void AddVisit(int index, Visit v, double time) throws RuntimeException {
		if(locked.size()>index && locked.get(index)){
			if(index>0 && departureTimes.get(index-1)==time){
				locked.set(index, false);
			}else
				throw new RuntimeException("Attempted to remove a locked visit");
		}
		locked.add(index,false);
		arrivalTimes.add(index,Double.MAX_VALUE);
		departureTimes.add(index,Double.MAX_VALUE);
		visits.add(index, v);
		updateTime(time);
	}
	
	
	/**
	 * Replace a visit with another
	 * @param index The index of the visit to replace
	 * @param v The visit to insert
	 * @param time The current time
	 * @return The visit that was just replaced
	 * @throws RuntimeException
	 */
	public Visit replaceVisit(int index, Visit v, double time) throws RuntimeException {
		if(locked.size()>index && locked.get(index)){
			if(index>0 && departureTimes.get(index-1)==time){
				locked.set(index, false);
			}else
				throw new RuntimeException("Attempted to remove a locked visit");
		}
		Visit old = visits.get(index);
		visits.set(index, v);
		updateTime(time);
		return old;
	}
	
	/**
	 * Update the locks
	 * @param time
	 */
	public void updateLocks(double time){
		// Update the timings for all other visits
		for(int i=1; i<visits.size(); i++){			
			// If we have already started driving, ensure we have locked
			if(departureTimes.get(i-1)<time){
				locked.set(i, true);
			}
		}
	}
	
	/**
	 * Update the timings of the arrivals and departures.
	 * @param time
	 */
	public void updateTime(double time){
		updateTimeDDARP(time);
	}	
	/**
	 * Updates the maximum speed of the vehicle
	 * @param speed
	 */
	public void setMaxSpeed(double speed){
		this.speed = speed;
	}
	
	public boolean isTimeFeasible(){
		return timeFeasible;
	}
	
	/**
	 * 
	 * @return The speed the vehicle is travelling at
	 */
	public static double getSpeed(){
		return speed;
	}
	
	/**
	 * 
	 * @return The distance traveled by the vehicle
	 */
	public double getDistance(){
		double time = 0;
		for(int i=0; i<visits.size()-1; i++){
			time += visits.get(i).getDistance(visits.get(i+1));
		}
		return time;
	}
	
	/**
	 * 
	 * @return The time used by the vehicle
	 */
	public double getTime(){
		return arrivalTimes.get(arrivalTimes.size()-1)-departureTimes.get(0);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTimeLabel(int index){
		String depart = "?";
		if(departureTimes.get(index)!=-1) depart = myFormatter.format(departureTimes.get(index));
		return myFormatter.format(arrivalTimes.get(index))+" -> "+depart;
	}
	
	/**
	 * Update timings using the drive first policy modified to respect ride time
	 * @param time
	 */
	private void updateTimeDDARP(double time){
		timeFeasible = updateBounds(1000000.0);

        // if updateBounds is false (there isn't exists a feasible time allocation) than do nothing
        if(!timeFeasible){
            return;
		}

		/*TODO you can update the function to deviate from drive first
		 * You may use the bounds added
		 * */
		
		// Update the departure time from the depot if it is not set yet
		if(visits.size()>1 && departureTimes.get(0)==-1.0){
			departureTimes.set(0, Math.max(0,time));
		}
		
		// Update the timings for all other visits
		for(int i=1; i<visits.size(); i++){
			// Check if we have information to set a new departure time for a locked visit
			if(locked.get(i) && departureTimes.get(i)==-1 && i<visits.size()-1){
				departureTimes.set(i, Math.max(Math.max(arrivalTimes.get(i)+5.0/60.0,time), lbDep.get(i) ));
			}
			
			// If the visit is not locked, it's arrival time must be updated
			if(!locked.get(i)){
				double travelTime = visits.get(i-1).getDistance(visits.get(i))/speed;
				arrivalTimes.set(i, departureTimes.get(i-1)+travelTime);
				
				if(i<visits.size()-1){ // Do we know where to go after the visit?
					departureTimes.set(i, Math.max(Math.max(arrivalTimes.get(i),visits.get(i).getTwStart())+5.0/60.0, lbDep.get(i)) );
				}
			}
						
			if(i==visits.size()-1){ // If we are at the last visit, wait
				departureTimes.set(i, -1.0);
			}
			
			if(arrivalTimes.get(i)>visits.get(i).getTwEnd() || (departureTimes.get(i)>=0 && departureTimes.get(i)<visits.get(i).getTwStart())){
				timeFeasible = false;
				return;
			}
		}
		
		// Check max ride time
		HashMap<Request, Double> timeStarted = new HashMap<Request, Double>();

		for(int i=1; i<visits.size(); i++){
			Visit v = visits.get(i);
			if(v.getVisitType()==VisitType.Pickup){
				double depart = Math.min(departureTimes.get(i)-5.0/60.0, visits.get(i).getTwEnd());
				timeStarted.put(v.getRequest(), depart);
			}
			if(v.getVisitType()==VisitType.Delivery){
				if(!timeStarted.containsKey(v.getRequest())){
					timeFeasible = false; // Delivery found before request
					return;
				}
				double starttime = timeStarted.get(v.getRequest());
				double endTime = Math.max(arrivalTimes.get(i), v.getTwStart());
				double rideTime = endTime-starttime;
				double directDrivingTime = v.getRequest().getPickup().getDistance(v)/speed;
				double maxRideTime = 10.0/60.0 + (directDrivingTime*2);
				if(rideTime-1.0/60>maxRideTime){ // One minute slack for numeric inaccuracy
					timeFeasible = false;
//					System.err.println("Ridetime failed ("+i+") "+rideTime+">"+maxRideTime+" mins: "+((rideTime-maxRideTime)*60.0));
//					System.err.println(this);
//					System.err.println();
				}
			}
		}
	}
	
	private boolean enforceMaxRideTime(){
		for(int i=1; i<visits.size(); i++){

			double drivingTime = visits.get(i-1).getDistance(visits.get(i))/speed;
			if(lbArr.get(i)<lbDep.get(i-1)+drivingTime){
				lbArr.set(i, lbDep.get(i-1)+drivingTime);
				
				if(lbArr.get(i)+5.0/60.0 > lbDep.get(i))
					lbDep.set(i,lbArr.get(i)+5.0/60.0);
			}
			
			if(visits.get(i).getVisitType()==VisitType.Delivery){
				
				double accDriving = 0;
				for(int j=i; j>=0; j--){
					// If we do not find the pickup for the delivery
					if(j==0)
						return false;
					
					// Have we found the pickup for our delivery?
					if(visits.get(j).getVisitType()==VisitType.Pickup && visits.get(j).getRequest()==visits.get(i).getRequest()){
						double directDrivingTime = visits.get(j).getDistance(visits.get(i))/speed;
						double maxRideTime = 10.0/60.0 + directDrivingTime*2; // // FIXME: 10/10/16 wtf????? skąd te liczby?

                        // If the max ride time is necessarily broken
						if(accDriving>maxRideTime){
							//System.out.println(accDriving+" > "+maxRideTime+" "+i+" "+j);
							return false;
						}
						
						// Set a bound on departure to ensure arrival time can be met
						double earliestService = Math.max(lbArr.get(i), visits.get(i).getTwStart());
						double newBound = earliestService-maxRideTime+5.0/60.0;
						if(lbDep.get(j)<newBound){
							lbDep.set(j,newBound);
							if(lbDep.get(i)>ubDep.get(i))
								return false;

							i=j-1;
						}
						
						break;
					}
					
					// update accumulated driving
					drivingTime = visits.get(j-1).getDistance(visits.get(j))/speed;
					accDriving += drivingTime+(5.0/60.0);
				}
			}
		}
		return true;
	}
	
	/**
	 * Update the lower bound on the arrival times, and the departure times, and the
	 * upper bound on the departure time.
	 * @return true iff there exists a feasible time allocation
	 * FIXME look at return
	 */
	public boolean updateBounds(double latestFinish){
		while(visits.size()>lbArr.size()){
			lbArr.add(0.0);
			lbDep.add(0.0);
			ubDep.add(0.0);
		}
		
		for(int i=0; i<visits.size(); i++){
			lbArr.set(i, 0.0);
			ubDep.set(i,latestFinish);
		}
		
		// Update lower bounds of arrival
		for(int i=1; i<visits.size(); i++){
			double drivingTime = visits.get(i-1).getDistance(visits.get(i))/speed;
			double lb = Math.max(lbArr.get(i-1), visits.get(i-1).getTwStart())+drivingTime+5.0/60.0; 
			lbArr.set(i,lb);
			// We cannot leave before we arrive + 5 mins
			double serviceStart = Math.max(lbArr.get(i), visits.get(i).getTwStart());
			lbDep.set(i,serviceStart+5.0/60.0);
		}
		
		// Update upper bounds on departure
		for(int i=visits.size()-2; i>=0; i--){
			double drivingTime = visits.get(i).getDistance(visits.get(i+1))/speed;
			double ub = Math.min(ubDep.get(i+1), visits.get(i+1).getTwEnd())-drivingTime;
			ubDep.set(i, ub);
			
			// We must leave in time to reach the next
			double twStart = visits.get(i).getTwStart();
			double lb = Math.max(Math.max(lbDep.get(i), lbArr.get(i+1)-drivingTime),  twStart); 
			lbDep.set(i,lb);
			
			if(ubDep.get(i)<lbDep.get(i))
				return false;
		}
		
		return enforceMaxRideTime();
	}
	
	/**
	 * Returns a string representation of the route.
	 */
	public String toString(){
		String str = "<Route:\n";
		for(int i=0; i<visits.size(); i++){
			Visit v = visits.get(i);
			str += "\t"+i+": "+" request: "+ v.getRequestId() +"\t";
			str += myFormatter.format(lbArr.get(i))+"\t"+myFormatter.format(lbDep.get(i))+"\t"+myFormatter.format(ubDep.get(i))+"\t";
			str += "At time: "+myFormatter.format(arrivalTimes.get(i))+"->"+myFormatter.format(departureTimes.get(i))+"\t";
			str += v  +"\t";
			
			//str += "\t"+i+": "+ v +" request: "+ v.getRequestId() +"\t"+ "At time: "+myFormatter.format(arrivalTimes.get(i))+"->"+myFormatter.format(departureTimes.get(i));
			if(locked.get(i)) str += " locked";
			str += "\n";
		}
		str += "\t"+visits.get(0);
		return str+">";
	}


	////////////////////////// new //////////////////
    // 19.10
	// method to check feasibility of a route
    public boolean feasibilityCheck() {
        if (!pickupDeliveryOrderCheck() || !checkTW() || !checkMaxRideTime()) {
            return false;
        }
        return true;
    }

    // methods checks if visits are in correct order
    // plus if both associated pick up and delivery are in the same route
    // returns true if delivery visit always follows corresponding pick up visit
    // doesn't take into consideration depots
    public boolean pickupDeliveryOrderCheck() {
        Visit v;
        int pickupIndex;
        int delIndex;
        boolean isOK = true;
        int x;

        //check if last visit is a depot - if so skip it in checking
        if (this.getVisit(this.size()-1).getVisitType() == VisitType.Depot) {
            x = 2;
        } else {
            x = 1;
        }

        for (int p = 1; p <= this.size() - x; p++) {// i from 1 to skip depot

            if (containsCorresponding(this.getVisit(p))) {
                v = findCorrespondingVisit(this.getVisit(p));

                if(this.getVisit(p).getVisitType() == VisitType.Pickup){
                    pickupIndex = findIndex(this.getVisit(p));
                    delIndex = findIndex(v);
                    if (v.getVisitType() != VisitType.Delivery || pickupIndex > delIndex) {
                        isOK = false;
                        break;
                    }
                } else if (this.getVisit(p).getVisitType() == VisitType.Delivery) {
                    delIndex = findIndex(this.getVisit(p));
                    pickupIndex = findIndex(v);
                    if (v.getVisitType() != VisitType.Pickup || pickupIndex > delIndex) {
                        isOK = false;
                        break;
                    }
                }


            } else {
                System.err.println("[pickupDeliveryOrderCheck] no corresponding visit to " +this.getVisit(p) + " request id: " +this.getVisit(p).getRequest().getId() +" type "+this.getVisit(p).getVisitType() + "\n"+this.getVisit(p).getRequest());
                System.err.println("\nfind corresponding: " +findCorrespondingVisit(this.getVisit(p)));
                return false;
            }
        }
        return isOK;
    }

    // method checks if a route consists of the same number of pick up and delivery visits
    public boolean equalNumber() {
        int pickUps = 0;
        int deliveries = 0;

        for (int p = 1; p <= this.size()-1; p++) {
            if (this.getVisit(p).getVisitType() == VisitType.Pickup) {
                pickUps++;
            }
        }
        for (int d = 1; d <= this.size()-1; d++) {
            if (this.getVisit(d).getVisitType() == VisitType.Delivery) {
                deliveries++;
            }
        }

        if (pickUps == deliveries){
            return true;
        } else {
            return false;
        }
    }

    // method finds index in a route for a given visit
    public int findIndex(Visit v) throws RuntimeException {
        if (this.visits.contains(v)) {
            for (int i = 0; i <= this.size() - 1; i++) {
                if (this.getVisit(i) == v) {
                    return i;
                }
            }
        }
        throw new RuntimeException("[findIndex] no such visit in ths route  \n"+v +"\nrequest "+ v.getRequest());
    }

    // method returns corresponding visit for an input visit
    // a corresponding visit is a visit with the same request id but different type (input visit pick up gives delivery visit)
    public Visit findCorrespondingVisit(Visit v) throws RuntimeException {
        int requestId = v.getRequestId();

        // used for debugging
        if (!this.visits.contains(v)) {
            throw new RuntimeException("this.visits.contains == false");
        } else if (!containsCorresponding(v)) {
//            throw new RuntimeException("containsCorresponding == false");
			System.out.print("containsCorresponding == false");
        }

        // if v is in this route and this route contains corresponding visit to v
        if (this.visits.contains(v) && containsCorresponding(v)) {
            if (v.getVisitType() == VisitType.Pickup) {
                for (int i = 1; i <= this.size() - 1; i++) {
                    if (this.getVisit(i).getRequestId() == requestId && this.getVisit(i).getVisitType() == VisitType.Delivery) {
                        return this.getVisit(i);
                    }
                }

            }else if (v.getVisitType() == VisitType.Delivery) {
                for (int i = 1; i <= this.size() - 1; i++) {
                    if (this.getVisit(i).getRequestId() == requestId && this.getVisit(i).getVisitType() == VisitType.Pickup) {
                        return this.getVisit(i);
                    }
                }
            }else { // if v is a depot
                if (this.findIndex(v) != 0 || this.findIndex(v) != this.size()-1) {
                    System.err.println("[findCorrespondingVisit] depot not only at the beginning and end of the route: "+v+ " place "+this.findIndex(v) + " route  "+this);
                } else if (this.findIndex(v) == 0 && this.getVisit(this.size()-1).getVisitType() == VisitType.Depot) {
                    return this.getVisit(this.size()-1); // return depot from the end
                } else if (this.findIndex(v) == this.size()-1 ) {
                    return this.getVisit(0); // if v is a depot from the end return depot from the beginning
                } else { // if !( this.getVisit(this.size()-1).getVisitType() == VisitType.Depot )
                    System.err.println("[findCorrespondingVisit] missing depot");
                }
            }
        } else {
            throw new RuntimeException("[findCorrespondingVisit] no visit "+v+" in this route. "    +"\n"+v.getRequest() );
        }
        throw new RuntimeException("[findCorrespondingVisit] unable to find corresponding visit to "+v.getVisitType() );
    }

    // method checks if route includes the corresponding visit for an input visit
    // returns true if a route includes
    public boolean containsCorresponding(Visit v) {
        int requestId = v.getRequestId();
        boolean contains = false;
        if (v.getVisitType() == VisitType.Pickup) {
            for (int i = 0; i <= this.size() - 1; i++) {
                if (this.getVisit(i) != v && this.getVisit(i).getRequestId() == requestId && this.getVisit(i).getVisitType() == VisitType.Delivery) {
                    return true;
                }
            }
            return false;

        } else if (v.getVisitType() == VisitType.Delivery) {
            for (int i = 0; i <= this.size() - 1; i++) {
                if (this.getVisit(i) != v && this.getVisit(i).getRequestId() == requestId && this.getVisit(i).getVisitType() == VisitType.Pickup) {
                    return true;
                }
            }
            return false;
        } else { // if v is a depot just return true...
//            if (this.findIndex(v) != 0 || this.findIndex(v) != this.size()-1) {
//                System.err.println("depot not only at the beginning and end of the route");
//            } else { // this.findIndex(v) == 0 || this.findIndex(v) == this.size()-1
//                if (this.getVisit(this.size()-1).getVisitType() == VisitType.Depot) {
//                    // since each route begins in depot just check if a depot is inserted at the end
//                    return true;
//                }
//            }
//            System.err.println("missing depot");
            return true; // ?????
        }
    }

    // method to check if a visit is in this route
    public boolean containsThisVisit(Visit v) {
        if (this.visits.contains(v)) {
            return true;
        } else {
            return false;
        }
    }

    ////////////////// 19.10 ///////////////////

    // method checks if each tw in this route is not violated
    // returns tru if everything is ok
    public boolean checkTW() {
        for (int i = 0; i <= size()-1; i++) {
            if (getArrivalTime(i) > getVisit(i).getTwEnd()) {
                System.out.println("TW broken for visit nr: "+i+ " visit " +getVisit(i) +" request id: " +getVisit(i).getRequestId() + " arrival time " +getArrivalTime(i));
                return false;
            }
        }
        return true;
    }

    // method checks if driving time for each customer does not exceed maximal allowed ride time
    // returns true if everything is ok
    public boolean checkMaxRideTime() {
        Visit v;
        int correspIndex;
        double drivingTime;
        double directRideTime;
        int x;
        if (getVisit(size()-1).getVisitType() == VisitType.Depot){
            x = 2;
        } else {
            x = 1;
        }
        for (int i = 1; i <= size()-x; i++) {
            if (getVisit(i).getIsFake()){
                // if this visit is fake - do not consider it
                continue;
            }
            v = findCorrespondingVisit(getVisit(i));
            correspIndex = findIndex(v);
            drivingTime = getArrivalTime(correspIndex) - getDepartureTime(i);
            directRideTime = getVisit(i).getDistance(v) / speed;
            if ( drivingTime > Math.max(30, directRideTime)) {
                System.err.println("driving time exceeds max ride time");
                return false;
            }
        }

        return true;
    }



}
