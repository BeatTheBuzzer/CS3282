package com.example.crowdmotoringdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ListView;

public class TransportInfoView extends Activity implements DataRetrieverResponse{
	
//	String stopId;
//	
//	StopViewListAdapter transportArray;
//	ListView transportList;
//	JSONArray transportArrayJson;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.stop_view);
//		
//		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
//		
//		transportList = (ListView) findViewById(R.id.list);
//		
//        
//        transportArray = new StopViewListAdapter(getApplicationContext(), R.layout.stop_view_list_element);
//        transportArray.stopId = stopId;
//        transportList.setAdapter(transportArray);
	}
	
	protected void onStart(){
		super.onStart();
		
//		System.out.println("onCreate finishing");
//		System.out.println(QueryBuilder.getBusInfo(stopId));
//		DataRetriever retriever = new DataRetriever();
//		retriever.caller = this;
//        retriever.execute(QueryBuilder.getBusInfo(stopId));
	}

	@Override
	public void onDataRetrieved(Object output) {
	}
}
