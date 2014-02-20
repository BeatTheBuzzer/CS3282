package com.example.crowdmotoringdemo;

import java.util.Comparator;

import org.json.JSONObject;

public class JSONComparatorByDistance implements Comparator<JSONObject> {
	public int compare(JSONObject lhs, JSONObject rhs){
		double distA = lhs.optDouble("distance");
		double distB = rhs.optDouble("distance");
		
		return Double.compare(distA, distB);
	}
}
