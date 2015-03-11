package com.synaptix.toast.gwt.client.controller.impl;

import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.ICellListController;
import com.synaptix.toast.gwt.client.controller.entry.IDomainController;

public class PageCellListController implements ICellListController<PageInfoDto> {

	private final IDomainController controller;

	public PageCellListController(IDomainController controller) {
		this.controller = controller;
	}

	@Override
	public void onCellChange(PageInfoDto e) {
		// controller.loadPageElements();
		controller.displayPageElements(e);
	}

}
