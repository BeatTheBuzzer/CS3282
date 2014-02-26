package com.example.crowdmotoringdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StopViewGetCrowdedness implements DataRetrieverResponse{
	protected StopViewListElement e;
	protected String stopId;
	protected volatile boolean isCurrent;
	
	public static void getCrowdedness(StopViewListElement e, String stopId){
		StopViewGetCrowdedness getter = new StopViewGetCrowdedness(e, stopId);
		getter.retrieveCrowdednessInfo();
	}
	
	protected StopViewGetCrowdedness(StopViewListElement e, String stopId){
		this.e = e;
		this.stopId = stopId;
		this.isCurrent = false;
	}
	
	protected void retrieveCrowdednessInfo(){
		DataRetriever retrieverHistorical = new DataRetriever();
		retrieverHistorical.setCallback(this);
        retrieverHistorical.execute(QueryBuilder.getCurrentCrowdedness(stopId, e.getRouteId()));
        
        DataRetriever retrieverCurrent = new DataRetriever();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getHistoricalCrowdedness(stopId, e.getRouteId()));
	}

	@Override
	public synchronized void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		if(requestStr.contains("history")){
			if(isCurrent) return; // current info supersedes historical info
			try {
				if(output == null){
					e.setCrowdedness(Constant.CROWDEDNESS_NO_DATA);
					return;
				}
				JSONArray historicalDataArr = new JSONArray((String)output);
				JSONObject historicalData = historicalDataArr.getJSONObject(0);
				int yes = historicalData.optInt("yes");
				int no = historicalData.optInt("no");
				if(yes == 0 && no == 0){
					e.setCrowdedness(Constant.CROWDEDNESS_NO_DATA);
					return;
				}
				if(yes > no) e.setCrowdedness(Constant.CROWDEDNESS_TRUE);
				else e.setCrowdedness(Constant.CROWDEDNESS_FALSE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (requestStr.contains("current")){
			try {
				if(output == null) return;
				JSONArray currentDataArr = new JSONArray((String)output);
				JSONObject currentData = currentDataArr.getJSONObject(0);
				String crowdedData = currentData.optString("crowded");
				isCurrent = true;
				if(crowdedData.equals("yes")) e.setCrowdedness(Constant.CROWDEDNESS_TRUE);
				else e.setCrowdedness(Constant.CROWDEDNESS_FALSE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
