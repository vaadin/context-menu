package com.vaadin.addon.contextmenu.widgetset.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.ui.VMenuBar;

/**
 * This is just to overcome the issue of application connection. Not needed
 * later, after this issue is resolved in the framework.
 */
public class MyVMenuBar extends VMenuBar {

	public MyVMenuBar() {
	}

	public MyVMenuBar(boolean subMenu, VMenuBar parentMenu) {
		super(subMenu, parentMenu);
	}

	// FIXME copy-paste from parent just to change VOverlay to MyVOverlay and
	// make this method visible to ContextMenuConnector
	protected void showChildMenuAt(CustomMenuItem item, int top, int left) {
		final int shadowSpace = 10;

		// FIXME only this line in changed
		popup = new MyVOverlay(true, false, true);
		popup.setOwner(this);

		/*
		 * Use parents primary style name if possible and remove the submenu
		 * prefix if needed
		 */
		String primaryStyleName = parentMenu != null ? parentMenu
				.getStylePrimaryName() : getStylePrimaryName();
		if (subMenu) {
			primaryStyleName = primaryStyleName.replace(
					SUBMENU_CLASSNAME_PREFIX, "");
		}
		popup.setStyleName(primaryStyleName + "-popup");

		// Setting owner and handlers to support tooltips. Needed for tooltip
		// handling of overlay widgets (will direct queries to parent menu)
		if (parentMenu == null) {
			popup.setOwner(this);
		} else {
			VMenuBar parent = parentMenu;
			while (parent.getParentMenu() != null) {
				parent = parent.getParentMenu();
			}
			popup.setOwner(parent);
		}
		if (client != null) {
			client.getVTooltip().connectHandlersToWidget(popup);
		}

		popup.setWidget(item.getSubMenu());
		popup.addCloseHandler(this);
		popup.addAutoHidePartner(item.getElement());

		// at 0,0 because otherwise IE7 add extra scrollbars (#5547)
		popup.setPopupPosition(0, 0);

		item.getSubMenu().onShow();
		visibleChildMenu = item.getSubMenu();
		item.getSubMenu().setParentMenu(this);

		popup.show();

		if (left + popup.getOffsetWidth() >= RootPanel.getBodyElement()
				.getOffsetWidth() - shadowSpace) {
			if (subMenu) {
				left = item.getParentMenu().getAbsoluteLeft()
						- popup.getOffsetWidth() - shadowSpace;
			} else {
				left = RootPanel.getBodyElement().getOffsetWidth()
						- popup.getOffsetWidth() - shadowSpace;
			}
			// Accommodate space for shadow
			if (left < shadowSpace) {
				left = shadowSpace;
			}
		}

		// top = adjustPopupHeight(top, shadowSpace);

		popup.setPopupPosition(left, top);

	}
}
