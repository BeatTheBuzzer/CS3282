package com.example.crowdmotoringdemo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.graphdrawer.CrowdednessGraphDrawer;
import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.MiscFunctions;
import com.example.crowdmotoringdemo.variables.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class TransportInfoActivity extends Activity implements ServerCommunicationCallback{
	
	final String crowdednessRealTimeNoReport = "Currently there is no report on the crowdedness of this bus.";
	final String crowdednessHistoricalNoReport = "Historically, there is no report on the crowdedness of this bus.";
	
	String stopId;
	String stopName;
	String transportName;
	int routeId;
	
	ArrayAdapter<String> spinnerChoice;
	String postMessage = "crowded";
	
	LinearLayout postCrowdednessDisplayLinearLayout;
	LinearLayout postCrowdednessInputLinearLayout;
	LinearLayout crowdednessGraphDisplayLinearLayout;
	LinearLayout crowdednessGraph;
	TextView transportInfoText;
	TextView realTimeText;
	TextView historicalText;
	TextView postCrowdednessArrowText;
	TextView crowdednessGraphArrowText;
	TextView crowdednessGraphNoDataText;
	Spinner postCrowdednessInputSpinner;
	Button crowdednessSubmitButton;
	
	Context mContext = this;
	
	CrowdednessGraphDrawer graphDrawer;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_info_activity);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		stopName = getIntent().getStringExtra(Constant.EXTRA_STOP_NAME);
		transportName = getIntent().getStringExtra(Constant.EXTRA_TRANSPORT_NAME);
		routeId = getIntent().getIntExtra(Constant.EXTRA_ROUTE_ID, -1);
		
		postCrowdednessDisplayLinearLayout = (LinearLayout) findViewById(R.id.postCrowdednessDisplayLinearLayout);
		postCrowdednessInputLinearLayout = (LinearLayout) findViewById(R.id.postCrowdednessInputLinearLayout);
		crowdednessGraphDisplayLinearLayout = (LinearLayout) findViewById(R.id.crowdednessGraphDisplayLinearLayout);
		crowdednessGraph = (LinearLayout) findViewById(R.id.crowdednessGraph);
		crowdednessGraphArrowText = (TextView) findViewById(R.id.crowdednessGraphArrowText);
		crowdednessGraphNoDataText = (TextView) findViewById(R.id.crowdednessGraphNoDataText);
		transportInfoText = (TextView) findViewById(R.id.transportInfoText);
		realTimeText = (TextView) findViewById(R.id.crowdednessRealTimeText);
		historicalText = (TextView) findViewById(R.id.crowdednessHistoricalText);
		postCrowdednessArrowText = (TextView) findViewById(R.id.postCrowdednessArrowText);
		postCrowdednessInputSpinner = (Spinner) findViewById(R.id.postCrowdednessInputSpinner);
		crowdednessSubmitButton = (Button) findViewById(R.id.crowdednessSubmitButton);
		
		spinnerChoice = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item_custom);
		spinnerChoice.add("crowded");
		spinnerChoice.add("uncrowded");
		postCrowdednessInputSpinner.setAdapter(spinnerChoice);
		
		graphDrawer = new CrowdednessGraphDrawer(this.stopId, this.routeId, this.crowdednessGraph, this);
		
		transportInfoText.setText(stopName + " - " + transportName);
		
		crowdednessGraphDisplayLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String upArrow = "\u25B2";
				String downArrow = "\u25BC";
				if(crowdednessGraphArrowText.getText().equals(upArrow)){
					crowdednessGraphArrowText.setText(downArrow);
					if(graphDrawer.graphIsDrawn()){
						crowdednessGraph.setVisibility(View.GONE);
					}
					crowdednessGraphNoDataText.setVisibility(View.GONE);
				}
				else{
					crowdednessGraphArrowText.setText(upArrow);
					if(graphDrawer.graphIsDrawn()){
						crowdednessGraph.setVisibility(View.VISIBLE);
						crowdednessGraphNoDataText.setVisibility(View.GONE);
					}
					else{
						crowdednessGraphNoDataText.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		postCrowdednessDisplayLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String upArrow = "\u25B2";
				String downArrow = "\u25BC";
				if(postCrowdednessArrowText.getText().equals(upArrow)){
					postCrowdednessArrowText.setText(downArrow);
					postCrowdednessInputLinearLayout.setVisibility(View.GONE);
					crowdednessSubmitButton.setVisibility(View.GONE);
				}
				else{
					postCrowdednessArrowText.setText(upArrow);
					postCrowdednessInputLinearLayout.setVisibility(View.VISIBLE);
					crowdednessSubmitButton.setVisibility(View.VISIBLE);
				}
			}
		});
		
		postCrowdednessInputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
            {
                postMessage = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) 
            {

            }
        });

		
		crowdednessSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerCommunication poster = new ServerCommunication();
				if(postMessage.equals("crowded")) poster.execute(QueryBuilder.post(routeId, stopId, true));
				else poster.execute(QueryBuilder.post(routeId, stopId, false));
		        Toast confirmation = Toast.makeText(getApplicationContext(), "Thanks for letting us know!", Properties.TOAST_DEFAULT_DURATION);
		        confirmation.show();
			}
		});
		
		new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("aaaa " + MiscFunctions.getAdvertisingId(mContext));
			}
			
		}.run();
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
		ServerCommunication retrieverCurrent = new ServerCommunication();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
        
        ServerCommunication retrieverHistorical = new ServerCommunication();
		retrieverHistorical.setCallback(this);
		retrieverHistorical.execute(QueryBuilder.getHistoricalCrowdedness(this.stopId,
				this.routeId,
				MiscFunctions.currentTimeStringBuilder(-10),
				MiscFunctions.currentTimeStringBuilder(+10),
				Properties.HISTORICAL_DATA_DEFAULT_AMOUNT
				));
		
		graphDrawer = new CrowdednessGraphDrawer(this.stopId, this.routeId, this.crowdednessGraph, this);
		graphDrawer.drawGraph(-20, 5, 15, -3, 3);
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
				int yes = 0;
				int no = 0;
				for(int i = 0; i < historicalDataArr.length();i++){
					JSONObject historicalData = historicalDataArr.getJSONObject(i);
					yes += historicalData.optInt("YES");
					no += historicalData.optInt("NO");
				}
				if(yes <= 0 && no <= 0){
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
				String sourceStopName = currentData.optString("name");
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
	
	public void onNoDataRetrieved(String requestStr){
		if(requestStr.contains("history")){
			historicalText.setText(crowdednessHistoricalNoReport);
		}
		else if (requestStr.contains("current")){
			realTimeText.setText(crowdednessRealTimeNoReport);
			return;
		}
	}
	
	protected String realTimeReportBuilder(boolean crowdedness, String time, String sourceStopName){
		String[] timeArr = time.split(":");
		System.out.println("time: " + time);
		String timeHour = Integer.parseInt(timeArr[0]) == 0? "":" " + Integer.parseInt(timeArr[0]) + " hours";
		String timeMinute = Integer.parseInt(timeArr[1]) == 0? "":" " + Integer.parseInt(timeArr[1]) + " minutes";
		String timeSecond = Integer.parseInt(timeArr[2]) == 0? "":" " + Integer.parseInt(timeArr[2]) + " seconds";
		return "The bus is reported " + (crowdedness?"crowded":"uncrowded") + " at " + sourceStopName + timeHour + timeMinute + timeSecond + " ago.";
	}
	
	protected String historicalReportBuilder(boolean crowdedness){
		return "Historically, the bus is reported " + (crowdedness?"crowded":"uncrowded") + " at this point of time.";
	}
}
