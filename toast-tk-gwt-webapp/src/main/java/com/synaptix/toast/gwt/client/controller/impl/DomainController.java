package com.synaptix.toast.gwt.client.controller.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.IDomainController;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.client.service.GreetingServiceAsync;
import com.synaptix.toast.gwt.client.view.impl.DomainTabViewImpl;

public class DomainController implements IDomainController {

	private final GreetingServiceAsync service = GWT.create(GreetingService.class);
	private DomainTabViewImpl view;
	private PageInfoDto currentPageInfo;

	@Override
	public void loadAllPages(final DomainTabViewImpl view) {
		String selectedJar = AdminController.getInstance().getSelectedJar();
		if (selectedJar == null) {
			Window.alert("Please select a jar from the admin tab first !");
		} else {
			service.loadPages(selectedJar, new AsyncCallback<List<PageInfoDto>>() {
				@Override
				public void onSuccess(List<PageInfoDto> result) {
					view.setPageList(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}

	@Override
	public void setView(DomainTabViewImpl domainTabViewImpl) {
		this.view = domainTabViewImpl;
	}

	@Override
	public void displayElementInfo(PageInfoDto e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayPageElements(PageInfoDto e) {
		this.currentPageInfo = e;
		view.showPageElements(e);
	}

	@Override
	public void displayElementInfo(ElementInfoDto e) {
		view.setElementDetails(e);
	}

	@Override
	public void onSaveElement(ElementInfoDto elementInfo) {
		service.saveElementInfo(currentPageInfo, elementInfo, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Window.alert("info saved !");
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
}
