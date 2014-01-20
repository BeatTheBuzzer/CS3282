package com.example.crowdmotoringdemo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class DataRetriever extends AsyncTask{

	@Override
	protected Object doInBackground(Object... arg0) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        
        String paramString = "";
        String url = "http://10.0.2.2/cs3282/php/test.php" + "?" + paramString;
        HttpGet httpGet = new HttpGet(url);

        try {
			httpResponse = httpClient.execute(httpGet);
			
			httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity);
            
            System.out.println(response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
	}

}
