package com.synaptix.toast.gwt.client.view.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.bean.ProjectInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.IReportController;
import com.synaptix.toast.gwt.client.view.entry.IReportTabView;

public class ReportTabViewImpl extends Composite implements IReportTabView {

	private static AdminTabViewImplUiBinder uiBinder = GWT.create(AdminTabViewImplUiBinder.class);

	interface AdminTabViewImplUiBinder extends UiBinder<Widget, ReportTabViewImpl> {
	}

	@UiField
	SimplePanel container;

	private final IReportController controller;

	public ReportTabViewImpl(IReportController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;
		this.controller.loadAllReports(this);
	}

	@UiHandler("loadButton")
	public void onClick(ClickEvent e) {
		controller.loadAllReports(this);
	}

	/**
	 * Set items for ListBox
	 * 
	 * @param listBox
	 * @param items
	 */
	public static final void setItemsListBox(ListBox listBox, List<String> items) {
		listBox.clear();
		if (items != null && !items.isEmpty()) {
			for (String item : items) {
				listBox.addItem(item);
			}
		}
	}

	@Override
	public void setReports(List<ProjectInfoDto> result) {
		VerticalPanel p = new VerticalPanel();
		for (ProjectInfoDto s : result) {
			p.add(new HTML(s.getName() + " " + s.getVersion() + " - excuted on: " + s.getExecutedOn() + " - total time (s): " + s.getTotalExecutionTime() + " <a href='/psc/reports?project="
					+ s.getName() + "&iteration=" + s.getInteration() + "'>details</a>"));
		}
		container.setWidget(p);
	}

}
