package com.synaptix.toast.gwt.client.view.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.ICellListController;
import com.synaptix.toast.gwt.client.controller.entry.IDomainController;
import com.synaptix.toast.gwt.client.controller.impl.ElementCellListController;
import com.synaptix.toast.gwt.client.controller.impl.PageCellListController;
import com.synaptix.toast.gwt.client.view.widget.ElementsInfoCellList;
import com.synaptix.toast.gwt.client.view.widget.PageInfoCellList;

public class DomainTabViewImpl extends Composite {

	private static DomainTabViewImplUiBinder uiBinder = GWT.create(DomainTabViewImplUiBinder.class);

	interface DomainTabViewImplUiBinder extends UiBinder<Widget, DomainTabViewImpl> {
	}

	@UiField
	Button refreshButton;

	@UiField
	SimplePanel pageList;

	@UiField
	SimplePanel pageElementList;

	@UiField
	SimplePanel elementInfoPanel;

	private final IDomainController controller;
	private final ICellListController<PageInfoDto> cellListController;

	public DomainTabViewImpl(final IDomainController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;
		controller.setView(this);
		this.cellListController = new PageCellListController(controller);
		refreshButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				controller.loadAllPages(DomainTabViewImpl.this);
			}
		});
	}

	public void setPageList(List<PageInfoDto> infos) {
		PageInfoCellList list = new PageInfoCellList(infos, cellListController);
		pageList.setWidget(list);
	}

	public void showPageElements(PageInfoDto e) {
		ElementsInfoCellList list = new ElementsInfoCellList(e.getElements(), new ElementCellListController(controller));
		pageElementList.setWidget(list);
	}

	public void setElementDetails(ElementInfoDto e) {
		ElementInfoPanelViewImpl infoPanel = new ElementInfoPanelViewImpl(controller);
		infoPanel.setDetails(e);
		elementInfoPanel.setWidget(infoPanel);
	}
}
