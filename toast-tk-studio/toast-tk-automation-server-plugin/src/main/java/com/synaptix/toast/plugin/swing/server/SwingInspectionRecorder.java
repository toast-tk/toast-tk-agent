package com.synaptix.toast.plugin.swing.server;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.input.InputState;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.guice.FilteredAWTEventListener;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.FocusEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.KeyboardEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.MouseEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.WindowEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobblerProvider;

public class SwingInspectionRecorder implements IEventRecorder {

	private static final Logger LOG = LogManager.getLogger(SwingInspectionRecorder.class);

	private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();

	private final InputState state;

	private final List<FilteredAWTEventListener> listeners;

	private List<AWTCapturedEvent> liveRecordedStepsBuffer;

	private EventStackGobbler currentEventStackGobbler;

	@Inject
	private ISwingInspectionServer cmdServer;

	private Set<FilteredAWTEventListener> customAwtListeners;

	@Inject
	private SwingInspectionRecorder(
		Set<FilteredAWTEventListener> customAwtListeners) {
		this.customAwtListeners = customAwtListeners;
		this.state = new InputState(DEFAULT_TOOLKIT);
		this.listeners = new ArrayList<FilteredAWTEventListener>();
		this.liveRecordedStepsBuffer = new ArrayList<AWTCapturedEvent>();
	}

	@Override
	public void startRecording()
		throws Exception {
		assertListenersIsEmpty();
		fillListeners();
		registerListeners();
	}

	private void fillListeners() {
		fillDefaultListeners();
		fillCustomListeners();
	}

	private void fillDefaultListeners() {
		listeners.add(recordKeybordEvents());
		listeners.add(recordMouseEvents());
		listeners.add(recordWindowEvents());
		listeners.add(recordFocusEvents());
	}

	private void fillCustomListeners() {
		if(customAwtListeners != null) {
			listeners.addAll(customAwtListeners);
		}
		else {
			LOG.info("No custom listeners have been defined");
		}
	}

	private void registerListeners() {
		for(final FilteredAWTEventListener listener : listeners) {
			registerListener(listener);
		}
	}

	private static void registerListener(
		final FilteredAWTEventListener listener) {
		DEFAULT_TOOLKIT.addAWTEventListener(listener, listener.getEventMask());
	}

	private void assertListenersIsEmpty() {
		if(!listeners.isEmpty()) {
			throw new IllegalStateException("listeners already active, stop recording first !");
		}
	}

	@Override
	public void stopRecording()
		throws Exception {
		unregisterListeners();
		clearListeners();
	}

	private void unregisterListeners() {
		for(final AWTEventListener listener : listeners) {
			unregisterListener(listener);
		}
	}

	private static void unregisterListener(
		final AWTEventListener listener) {
		DEFAULT_TOOLKIT.removeAWTEventListener(listener);
	}

	private void clearListeners() {
		listeners.clear();
	}

	public AWTCapturedEvent liveExplore(
		final List<AWTCapturedEvent> capturedEvents) {
		final List<AWTCapturedEvent> immutableLineList = ImmutableList.copyOf(capturedEvents);
		for(final AWTCapturedEvent capturedEvent : immutableLineList) {
			if(this.currentEventStackGobbler == null) {
				this.currentEventStackGobbler = EventStackGobblerProvider.get(capturedEvent);
			}
			if(currentEventStackGobbler != null){
				if(currentEventStackGobbler.isLooper()) {
					if(this.currentEventStackGobbler.digest(capturedEvent).isCompleted()) {
						EventType interpretedEventType = currentEventStackGobbler.getInterpretedEventType(capturedEvent);
						AWTCapturedEvent adjustedEvent = currentEventStackGobbler.getAdjustedEvent();
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
				LOG.info("No EventStackGobler for event: " + capturedEvent);
			}
		}
		return null;
	}

	private synchronized AWTCapturedEvent _process(
		EventType eventType,
		AWTCapturedEvent event) {
		event.setEventType(eventType);
		LOG.info("New Event Published: " + ToStringBuilder.reflectionToString(event));
		cmdServer.publishRecordEvent(event);
		liveRecordedStepsBuffer.clear();
		this.currentEventStackGobbler = null;
		return event;
	}

	private FilteredAWTEventListener recordFocusEvents() {
		return new FocusEventRecorder(state, this);
	}

	private FilteredAWTEventListener recordWindowEvents() {
		return new WindowEventRecorder(state, this);
	}

	private FilteredAWTEventListener recordMouseEvents() {
		return new MouseEventRecorder(state, this);
	}

	private FilteredAWTEventListener recordKeybordEvents() {
		return new KeyboardEventRecorder(state, this);
	}

	@Override
	public synchronized void appendInfo(
		final AWTCapturedEvent eventData) {
		try {
			liveRecordedStepsBuffer.add(eventData);
			liveExplore(liveRecordedStepsBuffer);
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public String getComponentLocator(
		Component component) {
		return cmdServer.getComponentLocator(component);
	}

	@Override
	public void scanUi(
		boolean debug) {
		this.cmdServer.scan(debug);
	}
	
	protected void setCommandServer(ISwingInspectionServer server){
		this.cmdServer = server;
	}
}
