package com.synaptix.toast.gwt.client.view.widget;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.ICellListController;

public class PageInfoCellList implements IsWidget {

	private final CellList<PageInfoDto> cellList;

	static class ContactCell extends AbstractCell<PageInfoDto> {

		/**
		 * The html of the image used for contacts.
		 */
		private String imageHtml;

		public ContactCell(ImageResource image) {
			// this.imageHtml = AbstractImagePrototype.create(image).getHTML();
		}

		@Override
		public void render(Context context, PageInfoDto value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}

			sb.appendHtmlConstant("<table class=\"page-info-cell\">");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			// sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</td></tr><tr><td>");
			sb.appendEscaped("Num# Elements: " + value.getNumElements());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	public PageInfoCellList(List<PageInfoDto> pages, final ICellListController<PageInfoDto> controller) {
		ContactCell contactCell = new ContactCell(null);

		cellList = new CellList<PageInfoDto>(contactCell);
		cellList.setPageSize(999);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		ListDataProvider<PageInfoDto> provider = new ListDataProvider<PageInfoDto>();
		provider.addDataDisplay(cellList);
		provider.setList(pages);
		// Add a selection model so we can select cells.
		final SingleSelectionModel<PageInfoDto> selectionModel = new SingleSelectionModel<PageInfoDto>();
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				controller.onCellChange(selectionModel.getSelectedObject());
				// contactForm.setContact();
			}
		});
	}

	@Override
	public Widget asWidget() {
		return cellList.asWidget();
	}

}
