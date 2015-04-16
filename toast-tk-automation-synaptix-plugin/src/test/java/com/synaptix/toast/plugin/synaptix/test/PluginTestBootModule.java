package com.synaptix.toast.plugin.synaptix.test;

import java.awt.Component;
import java.util.List;

import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.toast.core.guice.AbstractComponentFixtureModule;
import com.synaptix.toast.core.guice.AbstractFixtureModule;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;
import com.synaptix.toast.core.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.interpret.EventCapturedObject;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.ServiceCallHandler;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.TimelineHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ServiceCallCustomHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.SwingCustomWidgetHandler;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.AbstractEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.CenterCellDoubleClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.CenterCellRightClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.CenterCellSelectionEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineDoubleClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineRightClickEventRecorder;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.SimpleTimelineSelectionEventRecorder;

public class PluginTestBootModule extends AbstractComponentFixtureModule {

	@Override
	protected void configureModule() {
		bind(ISwingInspectionServer.class).to(SwingInspectionServerNop.class).in(Singleton.class);
		addTypeHandler(SwingCustomWidgetHandler.class, TimelineHandler.class);
		addTypeHandler(ServiceCallCustomHandler.class, ServiceCallHandler.class);
	}
	
	private class SwingInspectionServerNop implements ISwingInspectionServer {

		@Override
		public void highlight(String selectedValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getComponentLocator(Component component) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void publishRecordEvent(EventCapturedObject eventObject) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void publishInterpretedEvent(String sentence) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<String> scan(boolean b) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}