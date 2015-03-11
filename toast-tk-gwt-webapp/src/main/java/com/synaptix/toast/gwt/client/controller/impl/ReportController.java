package com.synaptix.toast.gwt.client.controller.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.bean.ProjectInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.IReportController;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.client.service.GreetingServiceAsync;
import com.synaptix.toast.gwt.client.view.entry.IReportTabView;

public class ReportController implements IReportController {
	private final GreetingServiceAsync service = GWT.create(GreetingService.class);

	private static final ReportController instance = new ReportController();

	private ReportController() {

	}

	public static ReportController getInstance() {
		return instance;
	}

	@Override
	public void loadAllReports(final IReportTabView view) {
		service.listAllReports(new AsyncCallback<List<ProjectInfoDto>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<ProjectInfoDto> result) {
				view.setReports(result);
			}
		});
	}

}
