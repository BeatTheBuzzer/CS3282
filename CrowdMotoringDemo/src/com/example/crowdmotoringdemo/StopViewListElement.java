package com.example.crowdmotoringdemo;

public class StopViewListElement {
	protected String transportName;
	protected int arrivalTimeMin;
	protected boolean crowdedness;
	protected boolean buttonShown;
	protected boolean toggleYes;
	protected boolean toggleNo;
	
	public StopViewListElement(){
		
	}
	
	public String getTransportName(){
		return transportName;
	}
	
	public int getArrivalTimeMin(){
		return arrivalTimeMin;
	}
	
	public boolean getCrowdedness(){
		return crowdedness;
	}
	
	public boolean getButtonShown(){
		return buttonShown;
	}
	
	public boolean getToggleYes(){
		return toggleYes;
	}
	
	public boolean getToggleNo(){
		return toggleNo;
	}
	
	public void setTransportName(String name){
		transportName = name;
	}
	
	public void setArrivalTimeMin(int time){
		arrivalTimeMin = time;
	}
	
	public void setCrowdedness(boolean crowded){
		if(!toggleYes && !toggleNo) crowdedness = crowded;
	}
	
	public void setButtonShown(boolean show){
		buttonShown = show;
	}
	
	public void setToggleYes(boolean toggle){
		if(toggle && !toggleNo){
			toggleYes = true;
			crowdedness = true;
		}
		else{
			toggleYes = false;
		}
	}
	
	public void setToggleNo(boolean toggle){
		if(toggle && !toggleYes){
			toggleNo = true;
			crowdedness = false;
		}
		else{
			toggleNo = false;
		}
	}
}
