package io.toast.tk.agent.web.record;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import io.toast.tk.agent.config.WebConfigProvider;
import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class WebRecorder {

	private static final Logger LOG = LogManager.getLogger(WebRecorder.class);
	private EventStackGobbler currentEventStackGobbler;
	private IAgentServer server;
	private WebConfigProvider configProvider;

	@Inject
	public WebRecorder(IAgentServer server, WebConfigProvider configProvider) {
		this.server = server;
		this.configProvider = configProvider;
	}

	public void liveExplore(final WebEventRecord capturedEvent) {
		if (Objects.isNull(this.currentEventStackGobbler)) {
			this.currentEventStackGobbler = EventStackGobblerProvider.get(capturedEvent);
			if (Objects.nonNull(this.currentEventStackGobbler)) {
				LOG.info(this.currentEventStackGobbler + " -> start <- "
						+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
			}
		}
		if (Objects.nonNull(this.currentEventStackGobbler)) {
			if (currentEventStackGobbler.isLooper()) {
				LOG.info(this.currentEventStackGobbler + " -> digest <- "
						+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
				if (this.currentEventStackGobbler.digest(capturedEvent).isCompleted()) {
					publishLoopEvent(capturedEvent);
					return;
				} 
				if (this.currentEventStackGobbler.isDirty()){
					retryInCleanState(capturedEvent);
				}
			} else {
				publishSimpleEvent(capturedEvent);
				return;
			}
		} else {
			LOG.info("No EventStackGobler for event: "
					+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
		}
	}

	private void publishSimpleEvent(final WebEventRecord capturedEvent) {
		LOG.info(this.currentEventStackGobbler + " -> completed-simple <- "
				+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
		EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
		_process(interpretedEventType, capturedEvent);
	}

	private void publishLoopEvent(final WebEventRecord capturedEvent) {
		LOG.info(this.currentEventStackGobbler + " -> completed-loop <- "
				+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
		EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
		WebEventRecord adjustedEvent = currentEventStackGobbler.getAdjustedEvent();
		_process(interpretedEventType, adjustedEvent);
	}

	private void retryInCleanState(final WebEventRecord capturedEvent) {
		LOG.info(this.currentEventStackGobbler + " -> dirty state <- "
				+ ToStringBuilder.reflectionToString(capturedEvent, ToStringStyle.SIMPLE_STYLE));
		this.currentEventStackGobbler.reset();
		this.currentEventStackGobbler = null;
		liveExplore(capturedEvent);
	}

	private WebEventRecord _process(
			final EventType interpretedEventType, 
			final WebEventRecord adjustedEvent) {
		this.currentEventStackGobbler.reset();
		this.currentEventStackGobbler = null;
		server.sendEvent(adjustedEvent, configProvider.get().getApiKey());
		return adjustedEvent;
	}

	public void process(
			WebEventRecord record) {
		try {
			liveExplore(record);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
