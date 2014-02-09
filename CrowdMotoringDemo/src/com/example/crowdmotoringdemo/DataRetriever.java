package com.example.crowdmotoringdemo;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;

public class DataRetriever extends AsyncTask<String, Object, String>{
	
	DataRetrieverResponse caller;

	@Override
	protected String doInBackground(String... paramString) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        
        System.out.println("Requesting data with parameter "+paramString[0]);
        
        String response = "";
        String url = Constant.URL_SERVER + paramString[0];
        HttpGet httpGet = new HttpGet(url);

        try {
			httpResponse = httpClient.execute(httpGet);
			
			httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            System.out.println("Got string " + response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return response;
	}
	
	protected void onPostExecute(String response){
		System.out.println("onPostExecute " + response);
		caller.onDataRetrieved(response);
	}

}
