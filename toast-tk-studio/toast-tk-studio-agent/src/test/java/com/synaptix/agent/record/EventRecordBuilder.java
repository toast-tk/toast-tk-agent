package com.synaptix.agent.record;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;

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
	
	public EventRecordBuilder withValue(String value){
		record.setValue(value);
		return this;
	}	
	
	public EventRecordBuilder target(String target){
		record.setTarget(target);
		return this;
	}

	public WebEventRecord build() {
		return record;
	}
	
}
