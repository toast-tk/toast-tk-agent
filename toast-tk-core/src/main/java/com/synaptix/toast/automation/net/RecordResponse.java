package com.synaptix.toast.automation.net;

import com.synaptix.toast.core.interpret.EventCapturedObject;


/**
 * Created by skokaina on 07/11/2014.
 */
public class RecordResponse implements IIdRequest{
	private String id;
	public EventCapturedObject value;
	private String sentence;

	/**
	 * serialization only
	 */
	public RecordResponse(){
		
	}
	
	public RecordResponse(EventCapturedObject eventObject) {
		this.value = eventObject;
	}

	public RecordResponse(String sentence){
		this.sentence = sentence;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public EventCapturedObject getEvent() {
		return value;
	}

	public String getSentence() {
		return sentence;
	}
	
}
