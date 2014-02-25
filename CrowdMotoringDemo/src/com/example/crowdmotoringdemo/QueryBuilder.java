package com.example.crowdmotoringdemo;

public class QueryBuilder {
	public static String getAllStops(){
		return "rquest=allstops";
	}
	
	public static String getBusInfo(String stopId){
		return "rquest=businfo&stop_id=" + stopId;
	}
	
	public static String post(String userId, String time, String content, String transportName, String stopId){
		return "rquest=provide&user_id=" + userId + "&time=" + time + "&content=" + content + "&number=" + transportName + "&stop=" + stopId;
	}
	
	public static String getCurrentCrowdedness(String stopId, String transportId){
		return "rquest=current&route_id=" + transportId + "stop_id=" + stopId;
	}
	
	public static String getHistoricalCrowdedness(String stopId, String transportId){
		return "rquest=current&route_id=" + transportId + "stop_id=" + stopId;
	}
}
