package com.synaptix.toast.core.net.response;

import java.util.List;

import com.synaptix.toast.core.net.request.IIdRequest;


/**
 * Created by skokaina on 07/11/2014.
 */
public class ScanResponse implements IIdRequest{
	private String id;
	public List<String> value;

	/**
	 * serialization only
	 */
	public ScanResponse(){
		
	}
	
	public ScanResponse(String id, List<String> componentList) {
		this.id = id;
		this.value = componentList;
	}

	@Override
	public String getId() {
		return id;
	}
	
	public List<String> getComponents() {
		return value;
	}
	
}
