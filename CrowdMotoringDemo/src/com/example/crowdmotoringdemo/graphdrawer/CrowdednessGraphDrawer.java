package com.example.crowdmotoringdemo.graphdrawer;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.MiscFunctions;
import com.example.crowdmotoringdemo.variables.Properties;

public class CrowdednessGraphDrawer implements ServerCommunicationCallback{
	final int yAxisMinValue = 0;
	final int yAxisMaxValue = 100;
	
	Activity caller;
	LinearLayout crowdednessGraph;
	
	volatile ArrayList<Double> points;
	volatile boolean graphIsDrawn;
	
	String stopId;
	int routeId;
	
	int totalPoints;
	ArrayList<String> xAxisLabels;
	
	public CrowdednessGraphDrawer(String stopId, int routeId, LinearLayout graph, Activity caller){
		this.stopId = stopId;
		this.routeId = routeId;
		this.crowdednessGraph = graph;
		this.caller = caller;
		this.points = new ArrayList<Double>();
		this.xAxisLabels = new ArrayList<String>();
		this.graphIsDrawn = false;
	}
	
	public boolean graphIsDrawn(){
		return this.graphIsDrawn;
	}
	
	public void drawGraph(int minOffsetStart, int pointDistance, int pointAmount, int pointOffsetStart, int pointOffsetEnd){
		this.xAxisLabels.clear();
		this.totalPoints = pointAmount;
		
		for(int i = 0; i < pointAmount; i++){
			int currentOffset = minOffsetStart + i*pointDistance;
			int currentOffsetStart = minOffsetStart + i*pointDistance + pointOffsetStart;
			int currentOffsetEnd = minOffsetStart + i*pointDistance + pointOffsetEnd;
//			xAxisLabels.add(MiscFunctions.currentTimeStringBuilder(currentOffset));
			xAxisLabels.add(currentOffset == 0? "Now":((currentOffset > 0? "+":"") + currentOffset));
			ServerCommunication retrieverHistorical = new ServerCommunication();
			retrieverHistorical.setCallback(this);
			retrieverHistorical.execute(QueryBuilder.getHistoricalCrowdedness(this.stopId,
					this.routeId,
					MiscFunctions.currentTimeStringBuilder(currentOffsetStart),
					MiscFunctions.currentTimeStringBuilder(currentOffsetEnd),
					Properties.HISTORICAL_DATA_DEFAULT_AMOUNT
					));
		}
	}
	
	protected void renderGraph(){
		final GraphicalView lineGraph;
		final String[] xAxisLabelsFinal = new String[this.totalPoints];
		
		for(int i = 0; i < this.points.size(); i++){
			if(Double.compare(this.points.get(i), 0.0) < 0 ||  Double.compare(this.points.get(i), 100.0) > 0){
				this.points.remove(i);
				this.xAxisLabels.remove(i);
				i--;
			}
		}
		
		if(this.points.size() <= 0){
			return;
		}
		
		for(int i = 0; i < this.xAxisLabels.size(); i++){
			xAxisLabelsFinal[i] = this.xAxisLabels.get(i);
		}
		
		XYSeries crowdednessPercentage = new XYSeries("Crowdedness Percentage");
		for(int i=0;i<this.points.size();i++)
		{
			DecimalFormat df= new DecimalFormat("0.00");
			String format = df.format(this.points.get(i));
			double finalValue = Double.parseDouble(format) ;
			crowdednessPercentage.add(i, finalValue);
		}
     
		XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
		dataset.addSeries(crowdednessPercentage);
     
     
		// Create XYSeriesRenderer to customize the line

		XYSeriesRenderer lineRenderer = new XYSeriesRenderer();
		lineRenderer.setColor(Color.RED);
		lineRenderer.setPointStyle(PointStyle.DIAMOND);
		lineRenderer.setDisplayChartValues(true);
		lineRenderer.setLineWidth(2);
		lineRenderer.setFillPoints(true);
		lineRenderer.setChartValuesTextSize(20);
     
		// Create XYMultipleSeriesRenderer to customize the whole graph

		XYMultipleSeriesRenderer graphRenderer=new XYMultipleSeriesRenderer();
     
		graphRenderer.setChartTitle("Crowdedness over time");
		graphRenderer.setXTitle("Time (minutes)");
     	graphRenderer.setYTitle("Percentage of being crowded");
     	graphRenderer.setXLabels(0);
     	graphRenderer.setBackgroundColor(Color.WHITE);
     	graphRenderer.setMarginsColor(Color.WHITE);
     	graphRenderer.setAxesColor(Color.parseColor(Constant.COLOR_LIGHT_BLACK));
     	graphRenderer.setLabelsColor(Color.parseColor(Constant.COLOR_LIGHT_BLACK));
     	graphRenderer.setXLabelsColor(Color.parseColor(Constant.COLOR_LIGHT_BLACK));
     	graphRenderer.setYLabelsColor(0, Color.parseColor(Constant.COLOR_LIGHT_BLACK));
     	graphRenderer.setPanEnabled(true, true);
     	graphRenderer.setZoomEnabled(true, true);
     	graphRenderer.setZoomButtonsVisible(true);
     	graphRenderer.setLabelsTextSize(24);
     	graphRenderer.setAxisTitleTextSize(28);
     	graphRenderer.setChartTitleTextSize(36);
     	graphRenderer.setLegendTextSize(28);
     	graphRenderer.setMargins(new int[]{48,48,48,48});
     	graphRenderer.setYAxisMax(100);
     	graphRenderer.setYAxisMin(0);
     	graphRenderer.setXAxisMin(this.points.size()/6);
     	graphRenderer.setXAxisMax(4*this.points.size()/6);
     	graphRenderer.setPanLimits(new double[]{0,this.points.size(),0,100});
     	graphRenderer.setZoomLimits(new double[]{0,this.points.size(),0,100});
   
     	graphRenderer.setShowGrid(true);
 
     	graphRenderer.setClickEnabled(true);
     
     	for(int i=0;i<points.size();i++)
     	{
     		graphRenderer.addXTextLabel(i, xAxisLabelsFinal[i]);
     	}
     	
     	graphRenderer.addSeriesRenderer(lineRenderer);
  

     	// Creating an intent to plot line chart using dataset and multipleRenderer
     
     	lineGraph = (GraphicalView)ChartFactory.getLineChartView(caller.getBaseContext(), dataset, graphRenderer);
     
     	//  Adding click event to the Line Chart.
     
     	lineGraph.setOnClickListener(new View.OnClickListener() {
   
    	@Override
    	public void onClick(View arg0) {
    		 // TODO Auto-generated method stub
    		try{
    			SeriesSelection selectedPoint = lineGraph.getCurrentSeriesAndPoint();
    
    			if(selectedPoint!=null)
    			{
    				String time = xAxisLabelsFinal[(int)selectedPoint.getXValue()];
     
    				double amount=(double)selectedPoint.getValue();
    				
    				Toast.makeText(caller.getBaseContext(), "Crowdedness at " + time + ": " + amount + "%", Properties.TOAST_DEFAULT_DURATION).show();
    			}
    		} catch(Exception e){
    			
    		}
    	}
     	});
     	// Add the graphical view mChart object into the Linear layout .
     	crowdednessGraph.addView(lineGraph);
     	this.graphIsDrawn = true;
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		try {
			if(output == null || ((String)output).length() <= 0){
				this.points.add(0.0);
				notify();
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
			
			if(yes+no > 0){
				this.points.add(100.0*yes/(yes+no));
			}
			else this.points.add(-1.0);
			
			if(this.points.size() >= this.totalPoints) this.renderGraph();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
