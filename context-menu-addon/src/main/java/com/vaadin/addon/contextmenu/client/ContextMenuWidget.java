package com.vaadin.addon.contextmenu.client;

import com.google.gwt.user.client.ui.Label;

// Extend any GWT Widget
public class ContextMenuWidget extends Label {

	public ContextMenuWidget() {

		// CSS class-name should not be v- prefixed
		setStyleName("context-menu2");

		// State is set to widget in ContextMenuConnector		
	}

}