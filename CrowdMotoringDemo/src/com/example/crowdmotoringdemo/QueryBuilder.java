package com.example.crowdmotoringdemo;

public class QueryBuilder {
	public static String getAllStops(){
		return "rquest=allstops";
	}
	
	public static String getBusInfo(String stopId){
		return "rquest=businfo&stop_id=" + stopId;
	}
}
