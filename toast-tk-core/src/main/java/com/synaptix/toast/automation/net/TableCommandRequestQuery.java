package com.synaptix.toast.automation.net;

public class TableCommandRequestQuery {

	public final String lookupCol;
	public final String lookupValue;
	public final String resultCol;

	public TableCommandRequestQuery(String lookupCol){
		this(lookupCol, null, null);
	}
	
	public TableCommandRequestQuery(String lookupCol, String lookupValue, String resultCol){
		this.lookupCol = lookupCol;
		this.lookupValue = lookupValue;
		this.resultCol = resultCol;
	}

	public TableCommandRequestQuery(String lookupCol, String lookupValue) {
		this(lookupCol, lookupValue, null);
	}
}
