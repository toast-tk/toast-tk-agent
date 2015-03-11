package com.synaptix.toast.gwt.client.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.controller.entry.IAppController;

public class ApplicationViewImpl extends Composite {

	private static ApplicationViewImplUiBinder uiBinder = GWT.create(ApplicationViewImplUiBinder.class);

	interface ApplicationViewImplUiBinder extends UiBinder<Widget, ApplicationViewImpl> {
	}

	private final IAppController controller;

	@UiField
	TabPanel tabPanel;

	public ApplicationViewImpl(IAppController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;

	}

	public void setAdminTabWidget(AdminTabViewImpl adminView) {
		tabPanel.add(adminView.asWidget(), "Administration");
	}

	public void setDomainTabWidget(DomainTabViewImpl domainView) {
		tabPanel.add(domainView.asWidget(), "Repository");
	}

	public void setTestTabWidget(TestTabViewImpl testTabView) {
		tabPanel.add(testTabView.asWidget(), "Web Script Base");
	}

	public void setTestGreenTabWidget(BackendTestPlanViewImpl backTabView) {
		tabPanel.add(backTabView.asWidget(), "Backend Script Base");
	}

	public void setReportTabWidget(ReportTabViewImpl reportTabViewImpl) {
		tabPanel.add(reportTabViewImpl.asWidget(), "Report");
		tabPanel.selectTab(0);
	}
}
