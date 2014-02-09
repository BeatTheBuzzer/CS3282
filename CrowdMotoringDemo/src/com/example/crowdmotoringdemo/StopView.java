package com.example.crowdmotoringdemo;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StopView extends Activity implements DataRetrieverResponse{
	
	String stopId;
	
	ArrayAdapter<String> transportArray;
	ListView transportList;
	JSONArray transportArrayJson;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_view);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		
		transportList = (ListView) findViewById(R.id.list);
        
        transportArray = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        transportList.setAdapter(transportArray);
		
		setContentView(R.layout.stop_view);
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getBusInfo(stopId));
		DataRetriever retriever = new DataRetriever();
		retriever.caller = this;
        retriever.execute(QueryBuilder.getBusInfo(stopId));
	}

	@Override
	public void onDataRetrieved(Object output) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json");
		
		try {
			transportArrayJson = new JSONArray((String)output);
			
			for(int i = 0; i < transportArrayJson.length(); i++){
				transportArray.add(transportArrayJson.optJSONObject(i).optString("name"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
