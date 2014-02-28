package com.example.crowdmotoringdemo;

public class StopListElement {
	protected String stopName;
	protected double stopDistance;
	
	public StopListElement(){
		
	}
	
	public String getName(){
		return stopName;
	}
	
	public double getDistance(){
		return stopDistance;
	}
	
	public void setName(String name){
		stopName = name;
	}
	
	public void setDistance(double distance){
		stopDistance = distance;
	}
}
