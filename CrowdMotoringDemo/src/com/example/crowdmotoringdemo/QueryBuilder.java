package com.example.crowdmotoringdemo;

public class QueryBuilder {
	public static String getAllStops(){
		return "rquest=allstops";
	}
	
	public static String getBusInfo(String stopId){
		return "rquest=businfo&stop_id=" + stopId;
	}
	
	public static String post(int route_id, String stop_id, boolean crowded){
		return "rquest=provide&route_id=" + route_id + "&stop_id=" + stop_id + "&crowded=" + (crowded?Constant.CROWDEDNESS_POST_TRUE:Constant.CROWDEDNESS_POST_FALSE);
	}
	
	public static String getCurrentCrowdedness(String stopId, int routeId){
		return "rquest=current&route_id=" + routeId + "&stop_id=" + stopId;
	}
	
	public static String getHistoricalCrowdedness(String stopId, int routeId){
		return "rquest=history&route_id=" + routeId + "&stop_id=" + stopId;
	}
}
