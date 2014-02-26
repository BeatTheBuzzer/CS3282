package com.example.crowdmotoringdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class TransportInfoView extends Activity implements DataRetrieverResponse{
	
	final String crowdednessRealTimeNoReport = "Currently there is no report on the crowdedness of this bus.";
	final String crowdednessHistoricalNoReport = "Historically, there is no report on the crowdedness of this bus.";
	
	String stopId;
	int routeId;
	
	TextView realTimeText;
	TextView historicalText;
	Switch crowdednessSwitch;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_info_view);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		routeId = getIntent().getIntExtra(Constant.EXTRA_ROUTE_ID, -1);
		
		realTimeText = (TextView) findViewById(R.id.crowdednessRealTimeText);
		historicalText = (TextView) findViewById(R.id.crowdednessHistoricalText);
		crowdednessSwitch = (Switch) findViewById(R.id.crowdednessSwitch);
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
		DataRetriever retrieverHistorical = new DataRetriever();
		retrieverHistorical.setCallback(this);
        retrieverHistorical.execute(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
        
        DataRetriever retrieverCurrent = new DataRetriever();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getHistoricalCrowdedness(stopId, routeId));
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		if(requestStr.contains("history")){
			try {
				if(output == null){
					historicalText.setText(crowdednessHistoricalNoReport);
					return;
				}
				JSONArray historicalDataArr = new JSONArray((String)output);
				JSONObject historicalData = historicalDataArr.getJSONObject(0);
				int yes = historicalData.optInt("yes");
				int no = historicalData.optInt("no");
				if(yes == 0 && no == 0){
					historicalText.setText(crowdednessHistoricalNoReport);
					return;
				}
				if(yes > no) historicalText.setText(historicalReportBuilder(true));
				else historicalText.setText(historicalReportBuilder(false));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (requestStr.contains("current")){
			try {
				if(output == null){
					realTimeText.setText(crowdednessRealTimeNoReport);
					return;
				}
				JSONArray currentDataArr = new JSONArray((String)output);
				JSONObject currentData = currentDataArr.getJSONObject(0);
				String crowdedData = currentData.optString("crowded");
				String time = currentData.optString("difference");
				if(crowdedData.equals("yes")) realTimeText.setText(realTimeReportBuilder(true, time));
				else realTimeText.setText(realTimeReportBuilder(false, time));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected String realTimeReportBuilder(boolean crowdedness, String time){
		String[] timeArr = time.split(":");
		String timeHour = timeArr[0] + " hours";
		String timeMinute = timeArr[1] + " minutes";
		String timeSecond = timeArr[2] + " seconds";
		return "The bus is reported " + (crowdedness?"crowded":"uncrowded") + " at " + stopId +" " + timeHour + " " + timeMinute + " " + timeSecond + " ago.";
	}
	
	protected String historicalReportBuilder(boolean crowdedness){
		return "Historically, the bus is reported " + (crowdedness?"crowded":"uncrowded") + " at this point of time.";
	}
}
