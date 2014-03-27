package com.example.crowdmotoringdemo.variables;

public class Properties {
	public static final int LOCATION_REFRESH_TIME = 5000; // milliseconds
	public static final int LOCATION_REFRESH_DISTANCE = 1; // metres
	public static final int HISTORICAL_DATA_DEFAULT_AMOUNT = 14; // days before today
	public static final int TOAST_DEFAULT_DURATION = 5000;
	public static final int TOAST_VERY_LONG_DURATION = 120000; // 2 minutes, enough time for asynctask to timeout if server is unreachable
}
