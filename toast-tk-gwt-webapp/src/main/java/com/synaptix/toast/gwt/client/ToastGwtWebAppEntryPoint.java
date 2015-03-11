package com.synaptix.toast.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.synaptix.toast.gwt.client.controller.impl.AppController;
import com.synaptix.toast.gwt.client.controller.impl.ReportController;
import com.synaptix.toast.gwt.client.view.impl.ApplicationViewImpl;
import com.synaptix.toast.gwt.client.view.impl.ReportTabViewImpl;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ToastGwtWebAppEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		ApplicationViewImpl app = new ApplicationViewImpl(new AppController());
		app.setReportTabWidget(new ReportTabViewImpl(ReportController.getInstance()));

		// app.setAdminTabWidget(new AdminTabViewImpl(AdminController.getInstance()));
		// app.setDomainTabWidget(new DomainTabViewImpl(new DomainController()));
		// app.setTestTabWidget(new TestTabViewImpl(new TestController()));
		// app.setTestGreenTabWidget(new BackendTestPlanViewImpl(new BackendTestController()));
		RootPanel.get("appContainer").add(app);
	}

}
