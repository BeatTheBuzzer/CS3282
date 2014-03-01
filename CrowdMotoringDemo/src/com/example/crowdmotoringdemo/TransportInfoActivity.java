package com.example.crowdmotoringdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class TransportInfoActivity extends Activity implements ServerCommunicationCallback{
	
	final String crowdednessRealTimeNoReport = "Currently there is no report on the crowdedness of this bus.";
	final String crowdednessHistoricalNoReport = "Historically, there is no report on the crowdedness of this bus.";
	
	String stopId;
	String stopName;
	String transportName;
	int routeId;
	
	TextView transportInfoText;
	TextView realTimeText;
	TextView historicalText;
	Button crowdednessTrueButton;
	Button crowdednessFalseButton;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_info_activity);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		stopName = getIntent().getStringExtra(Constant.EXTRA_STOP_NAME);
		transportName = getIntent().getStringExtra(Constant.EXTRA_TRANSPORT_NAME);
		routeId = getIntent().getIntExtra(Constant.EXTRA_ROUTE_ID, -1);
		
		transportInfoText = (TextView) findViewById(R.id.transportInfoText);
		realTimeText = (TextView) findViewById(R.id.crowdednessRealTimeText);
		historicalText = (TextView) findViewById(R.id.crowdednessHistoricalText);
		crowdednessTrueButton = (Button) findViewById(R.id.crowdednessTrueButton);
		crowdednessFalseButton = (Button) findViewById(R.id.crowdednessFalseButton);
		
		transportInfoText.setText(stopName + " - " + transportName);
		
		crowdednessTrueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerCommunication poster = new ServerCommunication();
		        poster.execute(QueryBuilder.post(routeId, stopId, true));
				crowdednessTrueButton.setVisibility(View.GONE);
				crowdednessFalseButton.setVisibility(View.GONE);
			}
		});
		
		crowdednessFalseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerCommunication poster = new ServerCommunication();
		        poster.execute(QueryBuilder.post(routeId, stopId, false));
				crowdednessTrueButton.setVisibility(View.GONE);
				crowdednessFalseButton.setVisibility(View.GONE);
			}
		});
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
		ServerCommunication retrieverHistorical = new ServerCommunication();
		retrieverHistorical.setCallback(this);
        retrieverHistorical.execute(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
        
        ServerCommunication retrieverCurrent = new ServerCommunication();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getHistoricalCrowdedness(stopId, routeId));
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		if(requestStr.contains("history")){
			try {
				if(output == null || ((String)output).length() <= 0){
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
				if(output == null || ((String)output).length() <= 0){
					realTimeText.setText(crowdednessRealTimeNoReport);
					return;
				}
				JSONArray currentDataArr = new JSONArray((String)output);
				JSONObject currentData = currentDataArr.getJSONObject(0);
				String sourceStopName = currentData.optString("stop_id");
				String crowdedData = currentData.optString("crowded");
				String time = currentData.optString("difference");
				if(crowdedData.equals("yes")) realTimeText.setText(realTimeReportBuilder(true, time, sourceStopName));
				else realTimeText.setText(realTimeReportBuilder(false, time, sourceStopName));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected String realTimeReportBuilder(boolean crowdedness, String time, String sourceStopName){
		String[] timeArr = time.split(":");
		System.out.println("time: " + time);
		String timeHour = timeArr[0] + " hours";
		String timeMinute = timeArr[1] + " minutes";
		String timeSecond = timeArr[2] + " seconds";
		return "The bus is reported " + (crowdedness?"crowded":"uncrowded") + " at " + sourceStopName +" " + timeHour + " " + timeMinute + " " + timeSecond + " ago.";
	}
	
	protected String historicalReportBuilder(boolean crowdedness){
		return "Historically, the bus is reported " + (crowdedness?"crowded":"uncrowded") + " at this point of time.";
	}
}
