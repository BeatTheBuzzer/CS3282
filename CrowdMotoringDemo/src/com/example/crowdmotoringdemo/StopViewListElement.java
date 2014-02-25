package com.example.crowdmotoringdemo;

public class StopViewListElement {
	protected String transportName;
	protected long arrivalTimeMin;
	protected int routeId;
	protected boolean crowdedness;
	
	public StopViewListElement(){
		
	}
	
	public String getTransportName(){
		return transportName;
	}
	
	public long getArrivalTimeMin(){
		return arrivalTimeMin;
	}
	
	public int getRouteId(){
		return routeId;
	}
	
	public boolean getCrowdedness(){
		return crowdedness;
	}
	
	public void setTransportName(String name){
		transportName = name;
	}
	
	public void setArrivalTimeMin(long time){
		arrivalTimeMin = time;
	}
	
	public void setRouteId(int id){
		routeId = id;
	}
	
	public void setCrowdedness(boolean crowded){
		crowdedness = crowded;
	}
}
