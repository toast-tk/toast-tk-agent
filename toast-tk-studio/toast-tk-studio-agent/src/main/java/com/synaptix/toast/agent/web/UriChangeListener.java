package com.synaptix.toast.agent.web;

public class UriChangeListener {
	
	private String location;

	public void onUriChange(String location){
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

}
