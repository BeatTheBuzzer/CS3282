package com.example.crowdmotoringdemo;

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

import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.MiscFunctions;

import android.app.Activity;
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
	Spinner postCrowdednessInputSpinner;
	Button crowdednessSubmitButton;
	Button crowdednessTrueButton;
	Button crowdednessFalseButton;
	
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
//		crowdednessGraph = (com.example.crowdmotoringdemo.holographlibrary.LineGraph) findViewById(R.id.crowdednessGraph);
		crowdednessGraph = (LinearLayout) findViewById(R.id.Chart_layout);
		crowdednessGraphArrowText = (TextView) findViewById(R.id.crowdednessGraphArrowText);
		transportInfoText = (TextView) findViewById(R.id.transportInfoText);
		realTimeText = (TextView) findViewById(R.id.crowdednessRealTimeText);
		historicalText = (TextView) findViewById(R.id.crowdednessHistoricalText);
		postCrowdednessArrowText = (TextView) findViewById(R.id.postCrowdednessArrowText);
		postCrowdednessInputSpinner = (Spinner) findViewById(R.id.postCrowdednessInputSpinner);
		crowdednessSubmitButton = (Button) findViewById(R.id.crowdednessSubmitButton);
		crowdednessTrueButton = (Button) findViewById(R.id.crowdednessTrueButton);
		crowdednessFalseButton = (Button) findViewById(R.id.crowdednessFalseButton);
		
		spinnerChoice = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item_custom);
		spinnerChoice.add("crowded");
		spinnerChoice.add("uncrowded");
		postCrowdednessInputSpinner.setAdapter(spinnerChoice);
		
		transportInfoText.setText(stopName + " - " + transportName);
		
		crowdednessGraphDisplayLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String upArrow = "\u25B2";
				String downArrow = "\u25BC";
				if(crowdednessGraphArrowText.getText().equals(upArrow)){
					crowdednessGraphArrowText.setText(downArrow);
					crowdednessGraph.setVisibility(View.GONE);
				}
				else{
					crowdednessGraphArrowText.setText(upArrow);
					crowdednessGraph.setVisibility(View.VISIBLE);
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
		        Toast confirmation = Toast.makeText(getApplicationContext(), "Thanks for letting us know!", 5000);
		        confirmation.show();
			}
		});
		
		crowdednessTrueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerCommunication poster = new ServerCommunication();
		        poster.execute(QueryBuilder.post(routeId, stopId, true));
		        Toast confirmation = Toast.makeText(getApplicationContext(), "Thanks for letting us know!", 5000);
		        confirmation.show();
			}
		});
		
		crowdednessFalseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ServerCommunication poster = new ServerCommunication();
		        poster.execute(QueryBuilder.post(routeId, stopId, false));
		        Toast confirmation = Toast.makeText(getApplicationContext(), "Thanks for letting us know!", 5000);
		        confirmation.show();
			}
		});
	}
	
	protected void onStart(){
		super.onStart();
		
		OpenChart();
//		drawHistoricalGraphMock();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
		ServerCommunication retrieverCurrent = new ServerCommunication();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getCurrentCrowdedness(stopId, routeId));
        
        ServerCommunication retrieverHistorical = new ServerCommunication();
		retrieverHistorical.setCallback(this);
        retrieverHistorical.execute(QueryBuilder.getHistoricalCrowdedness(stopId, routeId, MiscFunctions.currentTimeStringBuilder(-10), MiscFunctions.currentTimeStringBuilder(+10), 14));
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
				String sourceStopName = currentData.optString("stop_name");
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

	private void OpenChart()
    {
		final GraphicalView mChart;
		
		final String[] mMonth = new String[] {
		          "Jan", "Feb" , "Mar", "Apr", "May", "Jun",

		          "Jul", "Aug" };
		
     int z[]={0,1,2,3,4,5,6,7};
     
     
       int x[]={10,18,32,21,48,60,53,80};
     

      // Create XY Series for X Series.
     XYSeries xSeries=new XYSeries("X Series");
     

     //  Adding data to the X Series.
     for(int i=0;i<z.length;i++)
     {
      xSeries.add(z[i],x[i]);
   
     }

        // Create a Dataset to hold the XSeries.
     
     XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
     
      // Add X series to the Dataset.   
 dataset.addSeries(xSeries);
     
     
      // Create XYSeriesRenderer to customize XSeries

     XYSeriesRenderer Xrenderer=new XYSeriesRenderer();
     Xrenderer.setColor(Color.GREEN);
     Xrenderer.setPointStyle(PointStyle.DIAMOND);
     Xrenderer.setDisplayChartValues(true);
     Xrenderer.setLineWidth(2);
     Xrenderer.setFillPoints(true);
     
     // Create XYMultipleSeriesRenderer to customize the whole chart

     XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();
     
     mRenderer.setChartTitle("Crowdedness over time");
     mRenderer.setXTitle("Percentage of being crowded");
     mRenderer.setYTitle("Time");
     mRenderer.setZoomButtonsVisible(true);
     mRenderer.setXLabels(0);
     mRenderer.setPanEnabled(false);
     mRenderer.setBackgroundColor(Color.WHITE);
     mRenderer.setMarginsColor(Color.WHITE);
   
     mRenderer.setShowGrid(true);
 
     mRenderer.setClickEnabled(true);
     
     for(int i=0;i<z.length;i++)
     {
      mRenderer.addXTextLabel(i, mMonth[i]);
     }
     
       // Adding the XSeriesRenderer to the MultipleRenderer. 
     mRenderer.addSeriesRenderer(Xrenderer);
  
     
     LinearLayout chart_container=(LinearLayout)findViewById(R.id.Chart_layout);

   // Creating an intent to plot line chart using dataset and multipleRenderer
     
     mChart=(GraphicalView)ChartFactory.getLineChartView(getBaseContext(), dataset, mRenderer);
     
     //  Adding click event to the Line Chart.
     
     mChart.setOnClickListener(new View.OnClickListener() {
   
   @Override
   public void onClick(View arg0) {
    // TODO Auto-generated method stub
    
    SeriesSelection series_selection=mChart.getCurrentSeriesAndPoint();
    
    if(series_selection!=null)
    {
     int series_index=series_selection.getSeriesIndex();
     
     String select_series="X Series";
     if(series_index==0)
     {
      select_series="X Series";
     }else
     {
      select_series="Y Series";
     }
     
     String month=mMonth[(int)series_selection.getXValue()];
     
     int amount=(int)series_selection.getValue();
     
     Toast.makeText(getBaseContext(), select_series+"in" + month+":"+amount, Toast.LENGTH_LONG).show();
    }
   }
  });
     
// Add the graphical view mChart object into the Linear layout .
     chart_container.addView(mChart);
     
     
    }

	
	protected void drawHistoricalGraph(ArrayList<JSONObject> points){
//		for(int i = 0; i< points.size(); i++){
//		}
	}
}
