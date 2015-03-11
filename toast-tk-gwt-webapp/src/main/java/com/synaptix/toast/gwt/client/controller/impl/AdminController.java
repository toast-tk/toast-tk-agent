package com.synaptix.toast.gwt.client.controller.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.controller.entry.IAdminController;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.client.service.GreetingServiceAsync;
import com.synaptix.toast.gwt.client.view.entry.IAdminTabView;

public class AdminController implements IAdminController {
	private final GreetingServiceAsync service = GWT.create(GreetingService.class);

	private static final AdminController instance = new AdminController();

	private AdminController() {

	}

	public static AdminController getInstance() {
		return instance;
	}

	IAdminTabView view;

	@Override
	public void loadAllJars(final IAdminTabView view) {
		this.view = view;
		service.listAllJarsInUri(null, new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				view.setJars(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	public String getSelectedJar() {
		String jar = null;
		if (view == null) {
			jar = null;
		} else {
			jar = view.getSelectedJar();
		}
		return jar;
	}

}
