package com.synaptix.toast.gwt.client.view.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.synaptix.toast.gwt.client.view.impl.InspectorViewImpl;

public class InfoDialogBox extends DialogBox {

	public InfoDialogBox(final InspectorViewImpl widget, String url) {
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(widget);
		dialogVPanel.add(closeButton);

		setText("Inspecting: " + url);
		setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				InfoDialogBox.this.hide();
			}
		});

	}

	public InfoDialogBox(String content, String title) {
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(new HTML(content.replace("\n", "<br>")));
		dialogVPanel.add(closeButton);

		setText("Server result: " + title);
		setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				InfoDialogBox.this.hide();
			}
		});

	}

}
