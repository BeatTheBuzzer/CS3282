package com.example.crowdmotoringdemo.variables;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

public class MiscFunctions {
	// Outputs current time in server-specific format (hh:mm:ss), with minutes added by minOffset
	// Here hh is 24-hour format
	public static String currentTimeStringBuilder(int minOffset){
		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("SGT"));
		currentTime.set(Calendar.SECOND, 0);
		currentTime.add(Calendar.MILLISECOND, Constant.TIME_OFFSET);
		currentTime.add(Calendar.MINUTE, minOffset);
		System.out.println(currentTime.get(Calendar.HOUR_OF_DAY)+":"
							+currentTime.get(Calendar.MINUTE)+":"
							+currentTime.get(Calendar.SECOND));
		return currentTime.get(Calendar.HOUR_OF_DAY)+":"
							+(currentTime.get(Calendar.MINUTE) < 10?"0":"")
							+currentTime.get(Calendar.MINUTE)
							+":00";
	}
	
	// Outputs originalTime(hh:mm:ss) + minOffset()(minute)
	// Here hh is 24-hour format
	public static String addMinuteTimeStringBuilder(String originalTime, int minOffset){
		String[] timeArr = originalTime.split(":");
		int[] timeArrInt = {0, 0, 0};
		
		if(timeArr.length != 3) return "";
		
		for(int i = 0; i < timeArr.length; i++){
			timeArrInt[i] = Integer.parseInt(timeArr[i]);
		}
		
		timeArrInt[1] += minOffset;
		while(timeArrInt[1] > 60){
			timeArrInt[0]++;
			timeArrInt[1] -= 60;
		}
		while(timeArrInt[1] < 0){
			timeArrInt[0]--;
			timeArrInt[1] += 60;
		}
		while(timeArrInt[0] > 24){
			timeArrInt[0] -= 24;
		}
		while(timeArrInt[0] < 0){
			timeArrInt[0] += 24;
		}
		
		return (timeArrInt[0] < 10? "0":"") + timeArrInt[0] + ":"
				+ (timeArrInt[1] < 10? "0":"") + timeArrInt[1] + ":"
				+ (timeArrInt[2] < 10? "0":"") + timeArrInt[2];
	}
	
	public static double GPSDistance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
	    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) 
	    				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	    dist = Math.acos(dist);
	    dist = rad2deg(dist);
	    dist = dist * 60 * 1.1515;
	    dist = dist * 1.60934 * 1000; // Distance calculated above is in miles; convert to metres
	    return (dist);
	}
	
	public static String getAdvertisingId(Context mContext){ // Unique ID of each Android phone
		Info adInfo = null;
		
		String response = "lalala";
		
		try{
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
		} catch (Exception e) {
			return e.toString();
		}
		
//		return adInfo.getId();
		return response;
	}

	private static double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	private static double rad2deg(double rad) {
	    return (rad * 180.0 / Math.PI);
	}
}
