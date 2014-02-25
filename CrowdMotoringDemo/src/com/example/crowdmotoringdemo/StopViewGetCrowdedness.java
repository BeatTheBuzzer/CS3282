package com.example.crowdmotoringdemo;

public class StopViewGetCrowdedness implements DataRetrieverResponse{
	protected StopViewListElement e;
	protected String stopId;
	
	public static void getCrowdedness(StopViewListElement e, String stopId){
		StopViewGetCrowdedness getter = new StopViewGetCrowdedness(e, stopId);
		getter.retrieveCrowdednessInfo();
	}
	
	protected StopViewGetCrowdedness(StopViewListElement e, String stopId){
		this.e = e;
		this.stopId = stopId;
	}
	
	protected void retrieveCrowdednessInfo(){
		DataRetriever retriever = new DataRetriever();
		retriever.caller = this;
		retriever.execute(QueryBuilder.getHistoricalCrowdedness(stopId, e.getRouteId()));
        retriever.execute(QueryBuilder.getCurrentCrowdedness(stopId, e.getRouteId()));
	}

	@Override
	public void onDataRetrieved(Object output) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		// If historical
		// If current	
	}

}
