package com.vaadin.addon.contextmenu;

import com.vaadin.addon.contextmenu.client.ContextMenuClientRpc;
import com.vaadin.addon.contextmenu.client.ContextMenuServerRpc;
import com.vaadin.addon.contextmenu.client.ContextMenuState;

import com.vaadin.shared.MouseEventDetails;

// This is the server-side UI component that provides public API 
// for ContextMenu
public class ContextMenu extends com.vaadin.ui.AbstractComponent {

	private int clickCount = 0;

	// To process events from the client, we implement ServerRpc
	private ContextMenuServerRpc rpc = new ContextMenuServerRpc() {

		// Event received from client - user clicked our widget
		public void clicked(MouseEventDetails mouseDetails) {
			
			// Send nag message every 5:th click with ClientRpc
			if (++clickCount % 5 == 0) {
				getRpcProxy(ContextMenuClientRpc.class)
						.alert("Ok, that's enough!");
			}
			
			// Update shared state. This state update is automatically 
			// sent to the client. 
			getState().text = "You have clicked " + clickCount + " times";
		}
	};

	public ContextMenu() {

		// To receive events from the client, we register ServerRpc
		registerRpc(rpc);
	}

	// We must override getState() to cast the state to ContextMenuState
	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}
}
