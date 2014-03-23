package com.example.crowdmotoringdemo.servercommunication;

public interface ServerCommunicationCallback {
	public void onDataRetrieved(Object output, String requestStr);
	public void onNoDataRetrieved(String requestStr); // Called when the output is null
}
