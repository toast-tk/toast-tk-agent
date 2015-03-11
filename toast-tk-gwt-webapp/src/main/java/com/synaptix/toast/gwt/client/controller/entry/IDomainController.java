package com.synaptix.toast.gwt.client.controller.entry;

import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.view.impl.DomainTabViewImpl;

public interface IDomainController {

	public void loadAllPages(DomainTabViewImpl view);

	public void setView(DomainTabViewImpl domainTabViewImpl);

	public void displayElementInfo(PageInfoDto e);

	public void displayPageElements(PageInfoDto e);

	public void displayElementInfo(ElementInfoDto e);

	public void onSaveElement(ElementInfoDto elementInfo);

}
