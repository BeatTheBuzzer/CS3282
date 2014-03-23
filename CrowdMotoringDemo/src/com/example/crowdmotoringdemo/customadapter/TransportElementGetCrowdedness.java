package com.example.crowdmotoringdemo.customadapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.MiscFunctions;
import com.example.crowdmotoringdemo.variables.Properties;

import android.widget.ListView;

public class TransportElementGetCrowdedness implements ServerCommunicationCallback{
	protected TransportListAdapter adapter;
	protected ListView listView;
	protected TransportListElement e;
	protected String stopId;
	protected volatile boolean isCurrent;
	
	public static void getCrowdedness(TransportListElement e, String stopId, TransportListAdapter adapter, ListView listView){
		TransportElementGetCrowdedness getter = new TransportElementGetCrowdedness(e, stopId, adapter, listView);
		getter.retrieveCrowdednessInfo();
	}
	
	protected TransportElementGetCrowdedness(TransportListElement e, String stopId, TransportListAdapter adapter, ListView listView){
		this.e = e;
		this.stopId = stopId;
		this.isCurrent = false;
		this.adapter = adapter;
		this.listView = listView;
	}
	
	protected void retrieveCrowdednessInfo(){
		ServerCommunication retrieverCurrent = new ServerCommunication();
		retrieverCurrent.setCallback(this);
        retrieverCurrent.execute(QueryBuilder.getCurrentCrowdedness(stopId, e.getRouteId()));
        
        ServerCommunication retrieverHistorical = new ServerCommunication();
		retrieverHistorical.setCallback(this);
		retrieverHistorical.execute(QueryBuilder.getHistoricalCrowdedness(this.stopId,
				e.getRouteId(),
				MiscFunctions.currentTimeStringBuilder(-10),
				MiscFunctions.currentTimeStringBuilder(+10),
				Properties.HISTORICAL_DATA_DEFAULT_AMOUNT
				));
	}

	@Override
	public synchronized void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		if(requestStr.contains("history")){
			if(isCurrent) return; // current info supersedes historical info
			e.setIsHistorical(true);
			try {
				if(output == null){
					e.setCrowdedness(Constant.CROWDEDNESS_NO_DATA);
					e.setIsHistorical(false);
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
				if(yes == 0 && no == 0){
					e.setCrowdedness(Constant.CROWDEDNESS_NO_DATA);
					return;
				}
				if(yes > no){
					e.setCrowdedness(Constant.CROWDEDNESS_TRUE);
				}
				else e.setCrowdedness(Constant.CROWDEDNESS_FALSE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (requestStr.contains("current")){
			e.setIsHistorical(false);
			try {
				if(output == null || ((String)output).length() <= 0) return;
				JSONArray currentDataArr = new JSONArray((String)output);
				JSONObject currentData = currentDataArr.getJSONObject(0);
				String crowdedData = currentData.optString("crowded");
				isCurrent = true;
				if(crowdedData.equals("yes")) e.setCrowdedness(Constant.CROWDEDNESS_TRUE);
				else e.setCrowdedness(Constant.CROWDEDNESS_FALSE);
				System.out.println("crowdedness:" + e.getCrowdedness());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		listView.setAdapter(adapter);
	}

	public void onNoDataRetrieved(String requestStr){
		
	}
}
