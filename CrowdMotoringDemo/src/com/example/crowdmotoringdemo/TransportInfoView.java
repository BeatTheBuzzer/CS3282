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
	String transportId;
	
	TextView realTimeText;
	TextView historicalText;
	Switch crowdednessSwitch;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_info_view);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		transportId = getIntent().getStringExtra(Constant.EXTRA_TRANSPORT_ID);
		
		realTimeText = (TextView) findViewById(R.id.crowdednessRealTimeText);
		historicalText = (TextView) findViewById(R.id.crowdednessHistoricalText);
		crowdednessSwitch = (Switch) findViewById(R.id.crowdednessSwitch);
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getCurrentCrowdedness(stopId, transportId));
		DataRetriever retriever = new DataRetriever();
		retriever.caller = this;
        retriever.execute(QueryBuilder.getCurrentCrowdedness(stopId, transportId));
        System.out.println(QueryBuilder.getHistoricalCrowdedness(stopId, transportId));
        retriever.execute(QueryBuilder.getHistoricalCrowdedness(stopId, transportId));
	}

	@Override
	public void onDataRetrieved(Object output) {
		
		// If current info
		// If historical info
	}
}
