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
}
