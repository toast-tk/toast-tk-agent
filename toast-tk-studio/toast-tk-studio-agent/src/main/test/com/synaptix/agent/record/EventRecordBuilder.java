package com.synaptix.agent.record;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class EventRecordBuilder {

	WebEventRecord record;
	
	public EventRecordBuilder(){
		record = new WebEventRecord();
	}

	public EventRecordBuilder ofType(String type){
		record.type = type;
		return this;
	}
	
	public EventRecordBuilder component(String component){
		record.component = component;
		return this;
	}
	
	public EventRecordBuilder withValue(String value){
		record.value = value;
		return this;
	}	
	
	public EventRecordBuilder target(String target){
		record.target = target;
		return this;
	}

	public WebEventRecord build() {
		return record;
	}
	
}
