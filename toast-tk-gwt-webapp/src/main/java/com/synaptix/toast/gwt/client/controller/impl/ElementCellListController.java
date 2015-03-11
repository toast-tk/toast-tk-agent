package com.synaptix.toast.gwt.client.controller.impl;

import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.ICellListController;
import com.synaptix.toast.gwt.client.controller.entry.IDomainController;

public class ElementCellListController implements ICellListController<ElementInfoDto> {

	private final IDomainController controller;

	public ElementCellListController(IDomainController controller) {
		this.controller = controller;
	}

	@Override
	public void onCellChange(ElementInfoDto e) {
		controller.displayElementInfo(e);
	}

}
