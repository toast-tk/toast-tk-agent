package com.synaptix.toast.plugin.synaptix.runtime;

import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.toast.core.guice.AbstractComponentFixtureModule;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ServiceCallCustomWidgetHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.SwingCustomWidgetHandler;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.BaseEventInterpreter;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventInterpreter;
import com.synaptix.toast.plugin.synaptix.runtime.listener.TimelineFilteredAWTEventListener;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.AbstractEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineDoubleClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineRightClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineSelectionEventRecorder;

public class PluginModule extends AbstractComponentFixtureModule {

	Multibinder<AbstractEventRecorder> eventRecorderBinder;

	@Override
	protected void configureModule() {
		addTypeHandler(SwingCustomWidgetHandler.class);
		addTypeHandler(ServiceCallCustomWidgetHandler.class);
		addCustomFilteredAWTEventListener(TimelineFilteredAWTEventListener.class);
		bind(EventInterpreter.class).to(BaseEventInterpreter.class).in(Singleton.class);
		install(new MongoModule());
		addRecorder();
	}

	private void addRecorder() {
		this.eventRecorderBinder = Multibinder.newSetBinder(binder(), AbstractEventRecorder.class);
		addTimelineSelectionBinding(SimpleTimelineDoubleClickEventRecorder.class);
		addTimelineSelectionBinding(SimpleTimelineSelectionEventRecorder.class);
		addTimelineSelectionBinding(SimpleTimelineRightClickEventRecorder.class);
	}
	
	private <T extends AbstractEventRecorder> void addTimelineSelectionBinding(final Class<T> recorderClass) {
		bind(recorderClass).in(Singleton.class);
		eventRecorderBinder.addBinding().to(recorderClass);
	}
}