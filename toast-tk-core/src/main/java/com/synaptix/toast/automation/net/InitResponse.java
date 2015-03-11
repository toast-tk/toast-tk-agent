package com.synaptix.toast.automation.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skokaina on 07/11/2014.
 */
public class InitResponse implements IIdRequest {
    private String id;
    
    public String text;
    public List<String> items = new ArrayList<String>();
    
	@Override
	public String getId() {
		return id;
	}
}
