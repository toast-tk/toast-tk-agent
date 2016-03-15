package com.synaptix.toast.swing.agent.web.record;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.web.KryoAgentServer;

public class WebRecorder {

	private EventStackGobbler currentEventStackGobbler;
	private KryoAgentServer server;
	private List<WebEventRecord> liveRecordedStepsBuffer;

	public WebRecorder(KryoAgentServer server) {
		this.liveRecordedStepsBuffer = new ArrayList<WebEventRecord>();
		this.server = server;
	}

	public WebEventRecord liveExplore(
			final List<WebEventRecord> capturedEvents) {
			final List<WebEventRecord> immutableLineList = ImmutableList.copyOf(capturedEvents);
			for(final WebEventRecord capturedEvent : immutableLineList) {
				if(this.currentEventStackGobbler == null) {
					this.currentEventStackGobbler = EventStackGobblerProvider.get(capturedEvent);
				}
				if(currentEventStackGobbler != null){
					if(currentEventStackGobbler.isLooper()) {
						if(this.currentEventStackGobbler.digest(capturedEvent).isCompleted()) {
							EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
							WebEventRecord adjustedEvent = currentEventStackGobbler.getAdjustedEvent();
							currentEventStackGobbler.reset();
							return _process(interpretedEventType, adjustedEvent);
						}
						else {
							continue;
						}
					}
					else {
						EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
						return _process(interpretedEventType, capturedEvent);
					}
				}else{
					System.out.println("No EventStackGobler for event: " + capturedEvent);
				}
			}
			return null;
		}

	private WebEventRecord _process(EventType interpretedEventType,
			WebEventRecord adjustedEvent) {
		server.sendEvent(adjustedEvent);
		liveRecordedStepsBuffer.clear();
		this.currentEventStackGobbler = null;
		return adjustedEvent;
	}

	public void append(WebEventRecord record) {
		try {
			liveRecordedStepsBuffer.add(record);
			liveExplore(liveRecordedStepsBuffer);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}		
	}

}
