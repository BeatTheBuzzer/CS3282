package com.example.crowdmotoringdemo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	boolean clicked96 = false;
	boolean clickedCircle = false;
	boolean clicked183 = false;
	
	Button mButton96;
	Button mButtonCircleLine;
	Button mButton183;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton96 = (Button) findViewById(R.id.bus_96_button);
		mButtonCircleLine = (Button) findViewById(R.id.mrt_circle_button);
		mButton183 = (Button) findViewById(R.id.bus_183_button);
		
		mButton96.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clicked96) mButton96.setBackgroundColor(Color.rgb(255,165,0));
				else mButton96.setBackgroundResource(android.R.drawable.btn_default);
				clicked96 = !clicked96;
				
				// Test php call
				if(clicked96){
					new DataRetriever().execute();
				}
			}
		});
		
		mButtonCircleLine.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clickedCircle) mButtonCircleLine.setBackgroundColor(Color.rgb(255,165,0));
				else mButtonCircleLine.setBackgroundResource(android.R.drawable.btn_default);
				clickedCircle = !clickedCircle;
			}
		});

		mButton183.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clicked183) mButton183.setBackgroundColor(Color.rgb(255,165,0));
				else mButton183.setBackgroundResource(android.R.drawable.btn_default);
				clicked183 = !clicked183;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
