package com.synaptix.toast.core.record;

public class RecordedEvent {

	private String eventData;

	public RecordedEvent(String eventData) {
		this.setEventData(eventData);
	}

	public String getEventData() {
		return eventData;
	}

	private void setEventData(String eventData) {
		this.eventData = eventData;
	}

	private static RecordedEvent NO_RECORD_EVENT = new RecordedEvent("");
	
	public static RecordedEvent getNoRecordEvent() {
		return NO_RECORD_EVENT;
	}
}