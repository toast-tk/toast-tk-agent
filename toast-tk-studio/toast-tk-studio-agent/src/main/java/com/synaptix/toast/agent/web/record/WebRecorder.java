package com.synaptix.toast.agent.web.record;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.synaptix.toast.agent.web.IAgentServer;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class WebRecorder {

	private static final Logger LOG = LogManager.getLogger(WebRecorder.class);
	private EventStackGobbler currentEventStackGobbler;
	private IAgentServer server;
	private List<WebEventRecord> liveRecordedStepsBuffer;

	public WebRecorder(IAgentServer server) {
		this.liveRecordedStepsBuffer = new ArrayList<WebEventRecord>();
		this.server = server;
	}

	public WebEventRecord liveExplore(
			final List<WebEventRecord> capturedEvents) {
			final List<WebEventRecord> immutableLineList = ImmutableList.copyOf(capturedEvents);
			for(final WebEventRecord capturedEvent : immutableLineList) {
				if(this.currentEventStackGobbler == null) {
					this.currentEventStackGobbler = EventStackGobblerProvider.get(capturedEvent);
					LOG.info(this.currentEventStackGobbler + " -> start <- " + ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.DEFAULT_STYLE));
				}
				if(currentEventStackGobbler != null){
					if(currentEventStackGobbler.isLooper()) {
						LOG.info(this.currentEventStackGobbler + " -> digest <- " + ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.DEFAULT_STYLE));
						if(this.currentEventStackGobbler.digest(capturedEvent).isCompleted()) {
							LOG.info(this.currentEventStackGobbler + " -> completed-loop <- " + ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.DEFAULT_STYLE));
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
						LOG.info(this.currentEventStackGobbler + " -> completed-simple <- " + ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
						EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
						return _process(interpretedEventType, capturedEvent);
					}
				}else{
					LOG.info("No EventStackGobler for event: " 
						+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
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
			LOG.error(e);
		}		
	}
}
