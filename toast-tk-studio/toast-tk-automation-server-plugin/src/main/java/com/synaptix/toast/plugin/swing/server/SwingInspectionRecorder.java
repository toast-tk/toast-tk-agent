package com.synaptix.toast.plugin.swing.server;

import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.BRING_ON_TOP_DISPLAY;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.BUTTON_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.CHECKBOX_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.COMBOBOX_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.KEY_INPUT;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.MENU_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.POPUP_MENU_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.TABLE_CLICK;
import static com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType.WINDOW_DISPLAY;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isButtonType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isCheckBoxType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isComboBoxType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isMenuItemType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isMenuType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isPopupMenuType;
import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.isTableType;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.input.InputState;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.guice.FilteredAWTEventListener;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.FocusEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.KeyboardEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.MouseEventRecorder;
import com.synaptix.toast.plugin.swing.agent.record.WindowEventRecorder;

/**
 * Created by skokaina on 07/11/2014.
 */
public class SwingInspectionRecorder implements IEventRecorder {

	private static final Logger LOG = LogManager.getLogger(SwingInspectionRecorder.class);

	private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();

	private final InputState state;

	private final List<FilteredAWTEventListener> listeners;

	private List<AWTEventCapturedObject> liveRecordedStepsBuffer;

	private AWTEventCapturedObject eventObject;

	private String menu;

	@Inject
	private ISwingInspectionServer cmdServer;

	private Set<FilteredAWTEventListener> customAwtListeners;

	@Inject
	private SwingInspectionRecorder(Set<FilteredAWTEventListener> customAwtListeners) {
		this.customAwtListeners = customAwtListeners;
		this.state = new InputState(DEFAULT_TOOLKIT);
		this.listeners = new ArrayList<FilteredAWTEventListener>();
		this.liveRecordedStepsBuffer = new ArrayList<AWTEventCapturedObject>();
	}

	@Override
	public void startRecording() throws Exception {
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
		if (customAwtListeners != null) {
			listeners.addAll(customAwtListeners);
		} else {
			LOG.info("No custom listeners have been defined");
		}
	}

	private void registerListeners() {
		for (final FilteredAWTEventListener listener : listeners) {
			registerListener(listener);
		}
	}

	private static void registerListener(final FilteredAWTEventListener listener) {
		DEFAULT_TOOLKIT.addAWTEventListener(listener, listener.getEventMask());
	}

	private void assertListenersIsEmpty() {
		if (!listeners.isEmpty()) {
			throw new IllegalStateException("listeners already active, stop recording first !");
		}
	}

	@Override
	public void stopRecording() throws Exception {
		unregisterListeners();
		clearListeners();
	}

	private void unregisterListeners() {
		for (final AWTEventListener listener : listeners) {
			unregisterListener(listener);
		}
	}

	private static void unregisterListener(final AWTEventListener listener) {
		DEFAULT_TOOLKIT.removeAWTEventListener(listener);
	}

	private void clearListeners() {
		listeners.clear();
	}

	private void consumeEventLine(final AWTEventCapturedObject capturedEvent) {
		String locator = capturedEvent.componentLocator;
		String name = capturedEvent.componentName;
		String type = capturedEvent.componentType;
		String value = capturedEvent.businessValue;
		String container = capturedEvent.container;
		long timeStamp = capturedEvent.timeStamp;
		eventObject = new AWTEventCapturedObject(container, locator, name, type, value, timeStamp);
	}

	
	public void liveExplore(final List<AWTEventCapturedObject> capturedEvents) {
		// TO BE MOUVED INSIDE THE INPUT CAPTURE SECTION
		boolean inputCaptureOpened = false;
		boolean menuCaptureOpened = false;
		boolean comboboxCaptureOpened = false;
		String inputTypeUnderCapture = null;
		// //////////////////////////////////////////////

		final List<AWTEventCapturedObject> immutableLineList = ImmutableList.copyOf(capturedEvents);
		for(final AWTEventCapturedObject capturedEvent : immutableLineList) {
			consumeEventLine(capturedEvent);
			
			String locator = eventObject.componentLocator;
			String name = eventObject.componentName;
			String type = eventObject.componentType;

			if (inputCaptureOpened) {
				if (capturedEvent.isInputEvent() || capturedEvent.isMouseClickEvent()) {
					continue;
				} 
				else if (capturedEvent.isFocusLostEvent()) {
					String inputCapturedType = capturedEvent.componentType;
					if (inputCapturedType.equals(inputTypeUnderCapture)) {
						eventObject.componentName = "null".equals(name) || name == null ? locator : name;
						eventObject.businessValue = capturedEvent.businessValue;
						_process(KEY_INPUT);
					}
				} 
				else {
					inputTypeUnderCapture = null;
					inputCaptureOpened = false;
				}
			}

			if (menuCaptureOpened) {
				if (eventObject.isMouseClickEvent()) {
					String subTargetType = capturedEvent.componentType;
					if (isMenuItemType(subTargetType)) {
						eventObject.componentName = menu + " / " + capturedEvent.componentName;
						_process(MENU_CLICK);
						menuCaptureOpened = false;
					}
				} 
				else {
					eventObject.componentName = menu;
					_process(MENU_CLICK);
					menuCaptureOpened = false;
				}
				continue;
			}

			if (comboboxCaptureOpened) {
				if (capturedEvent.isFocusLostEvent()) {
					eventObject.componentLocator = capturedEvent.componentLocator;
					eventObject.componentName = name == null || "null".equals(name) ? eventObject.componentLocator : name;
					eventObject.businessValue = capturedEvent.businessValue;
					_process(COMBOBOX_CLICK);
					comboboxCaptureOpened = false;
				}
				continue;
			}

			// Opening key loop to capture final input value
			if (capturedEvent.isInputEvent()) {
				inputCaptureOpened = true;
				inputTypeUnderCapture = capturedEvent.componentType;
				continue;
			}
			// Opening key loop to capture menu
			else if (isMenuType(type) && capturedEvent.isMouseClickEvent()) {
				menuCaptureOpened = true;
				menu = eventObject.componentName;
				continue;
			}
			// Opening key loop to capture combobox value
			else if (isComboBoxType(type) && capturedEvent.isMouseClickEvent()) {
				comboboxCaptureOpened = true;
				continue;
			} 
			else {
				/* LOOKING FOR NEW PANEL FOCUS STEP */
				if (capturedEvent.isFocusGainedEvent()) {
					_process(BRING_ON_TOP_DISPLAY);
				}

				/* LOOKING FOR NEW DIALOG FOCUS STEP */
				if (capturedEvent.isWindowClickEvent()) {
					_process(WINDOW_DISPLAY);
				}

				/* LOOKING FOR MOUSE CLICK STEPS */
				if(capturedEvent.isMouseClickEvent()) {
					if(isButtonType(eventObject.componentType)) {
						_process(BUTTON_CLICK);
					} 
					else if(isCheckBoxType(eventObject.componentType)) {
						_process(CHECKBOX_CLICK);
					}
					else if(isTableType(type)) {
						_process(TABLE_CLICK);
					} 
					else if(isPopupMenuType(type)) {
						_process(POPUP_MENU_CLICK);
					}
					/*else if(isJListType(type)) {
						_process(POPUP_MENU_CLICK);
					}*/
				}
			}
		}
	}

	private synchronized void _process(EventType eventType) {
		eventObject.setEventType(eventType);
		cmdServer.publishRecordEvent(eventObject);
		liveRecordedStepsBuffer.clear();
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
	public synchronized void appendInfo(final AWTEventCapturedObject eventData) {
		try {
			liveRecordedStepsBuffer.add(eventData);
			liveExplore(liveRecordedStepsBuffer);
		} 
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public String getComponentLocator(Component component) {
		return cmdServer.getComponentLocator(component);
	}

	@Override
	public void scanUi(boolean b) {
		cmdServer.scan(b);
	}

}
