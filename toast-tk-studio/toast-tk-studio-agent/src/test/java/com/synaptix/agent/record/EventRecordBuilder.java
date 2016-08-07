package com.synaptix.agent.record;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public class EventRecordBuilder {

	WebEventRecord record;
	
	public EventRecordBuilder(){
		record = new WebEventRecord();
	}

	public EventRecordBuilder ofType(String type){
		record.setEventType(type);
		return this;
	}
	
	public EventRecordBuilder component(String component){
		record.setComponent(component);
		return this;
	}
	
	public EventRecordBuilder val(String value){
		record.setValue(value);
		return this;
	}	
	
	private EventRecordBuilder target(String target){
		record.setTarget(target);
		return this;
	}
	
	public EventRecordBuilder locator(String locator){
		return target(locator);
	}
	

	public EventRecordBuilder changeEvent(){
		return ofType("change");
	}
	
	public EventRecordBuilder focusEvent(){
		return ofType("focus");
	}
	
	public EventRecordBuilder input(){
		return component("text");
	}

	public WebEventRecord build() {
		return record;
	}
	
}
