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
	
	final String crowdednessRealTimeReport = "The bus is reported |crowdedness| at |stopId| |time| ago.";
	final String crowdednessRealTimeNoReport = "Currently there is no report on the crowdedness of this bus.";
	final String crowdednessHistoricalReport = "Historically, the bus is reported |crowdedness| at this point of time.";
	
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
		
		// If current info
		// If historical info
	}
}
