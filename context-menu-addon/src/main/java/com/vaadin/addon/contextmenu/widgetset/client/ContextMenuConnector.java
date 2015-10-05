package com.vaadin.addon.contextmenu.widgetset.client;

import java.util.List;
import java.util.logging.Logger;

import com.example.contextmenu.menubar.ContextMenu;
import com.example.contextmenu.widgetset.client.MenuSharedState.MenuItemState;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.client.ui.VMenuBar.CustomMenuItem;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
	private static Logger logger = Logger.getLogger("ContextMenuConnector");
	
	// TODO: make it so that we don't need this dummy root menu bar.
	private MyVMenuBar dummyRootMenuBar;
	private VMenuBar contextMenuWidget;

	@Override
	public MenuSharedState getState() {
		return (MenuSharedState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		logger.severe("onStateChange");
		
		super.onStateChanged(stateChangeEvent);

		contextMenuWidget.clearItems();
		addMenuItemsFromState(contextMenuWidget, getState().menuItems);
	}
	
	@Override
	protected void init() {
		super.init();

		dummyRootMenuBar = new MyVMenuBar();

		CustomMenuItem item = new CustomMenuItem();
		dummyRootMenuBar.getItems().add(item);

		contextMenuWidget = new MyVMenuBar(true, dummyRootMenuBar);
		item.setSubMenu(contextMenuWidget);

		// application connection that is used for all our overlays
		MyVOverlay.setApplicationConnection(this.getConnection());
		
		registerRpc(ContextMenuClientRpc.class, new ContextMenuClientRpc() {
			@Override
			public void showContextMenu(int x, int y) {
				showMenu(x, y);
			}
		});
	}

	private void addMenuItemsFromState(VMenuBar menuToAddTo,
			List<MenuItemState> menuItems) {
		if (menuItems == null)
			return;
		
		for (MenuItemState menuItemState : menuItems) {
			CustomMenuItem newItem = addMenuItemToMenu(menuToAddTo, menuItemState);
			
			if (menuItemState.childItems != null && menuItemState.childItems.size() > 0) {
				VMenuBar subMenu = new VMenuBar(true, menuToAddTo);
				addMenuItemsFromState(subMenu, menuItemState.childItems);
				newItem.setSubMenu(subMenu);
			}
		}
	}

	private CustomMenuItem addMenuItemToMenu(VMenuBar menuToAddTo,
			final MenuItemState menuItemState) {
		CustomMenuItem item = menuToAddTo.addItem(menuItemState.text, new Command() {
			@Override
			public void execute() {
				dummyRootMenuBar.hideChildren();
				itemSelected(menuItemState.id);
			}
		});
		// the rest of the state...
		return item;
	}

	protected void itemSelected(int id) {
		getRpcProxy(ContextMenuServerRpc.class).itemClicked(id, true);
	}

	private void showMenu(int eventX, int eventY) {
		dummyRootMenuBar.showChildMenuAt(dummyRootMenuBar.getItems().get(0), eventY, eventX);
	}

	@Override
	protected void extend(ServerConnector target) {
		Logger.getLogger("ContextMenuConnector").info("extend");
		
		Widget widget = ((AbstractComponentConnector) target).getWidget();
		
//		widget.addDomHandler(new ContextMenuHandler() {
//
//			@Override
//			public void onContextMenu(ContextMenuEvent event) {
//				event.stopPropagation();
//				event.preventDefault();
//				
//				showMenu(event.getNativeEvent().getClientX(), event
//						.getNativeEvent().getClientY());
//			}
//		}, ContextMenuEvent.getType());

		widget.addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// FIXME: check if menu is shown or handleNavigation will do it?
								
				boolean handled = dummyRootMenuBar.handleNavigation(event.getNativeEvent()
						.getKeyCode(), event.getNativeEvent().getCtrlKey(),
						event.getNativeEvent().getShiftKey());
				
				if (handled) {
					event.stopPropagation();
					event.preventDefault();
				}
			}
		}, KeyDownEvent.getType());
	}
}
