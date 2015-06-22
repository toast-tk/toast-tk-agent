package com.synaptix.toast.core.net.response;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.net.request.IIdRequest;


/**
 * Created by skokaina on 07/11/2014.
 */
public class RecordResponse implements IIdRequest{
	private String id;
	public AWTEventCapturedObject value;
	private String sentence;

	/**
	 * serialization only
	 */
	public RecordResponse(){
		
	}
	
	public RecordResponse(AWTEventCapturedObject eventObject) {
		this.value = eventObject;
	}

	public RecordResponse(String sentence){
		this.sentence = sentence;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public AWTEventCapturedObject getEvent() {
		return value;
	}

	public String getSentence() {
		return sentence;
	}
	
}
