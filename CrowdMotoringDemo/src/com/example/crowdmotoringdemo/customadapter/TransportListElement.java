package com.example.crowdmotoringdemo.customadapter;

public class TransportListElement {
	protected String transportName;
	protected long arrivalTimeMin;
	protected int routeId;
	protected int crowdedness;
	protected boolean isHistorical;
	
	public TransportListElement(){
		
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
	
	public int getCrowdedness(){
		return crowdedness;
	}
	
	public boolean getIsHistorical(){
		return isHistorical;
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
	
	public void setCrowdedness(int crowded){
		crowdedness = crowded;
	}
	
	public void setIsHistorical(boolean historical){
		isHistorical = historical;
	}
}
