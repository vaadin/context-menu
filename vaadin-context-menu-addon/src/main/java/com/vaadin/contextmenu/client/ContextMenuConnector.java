/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.contextmenu.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.HasWidget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;

import java.util.Iterator;
import java.util.Stack;

@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector
        implements HasWidget {

    private VContextMenu contextMenu;
    private VMenuBar.CustomMenuItem contextMenuRoot;

    @Override
    protected void extend(ServerConnector target) {

    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        contextMenu.client = getConnection();
        updateFromState(getState());
    }

    protected void updateFromState(ContextMenuState state) {

        contextMenu.enabled = state.enabled;
        contextMenu.htmlContentAllowed = state.htmlContentAllowed;

        if (null != getState()
                && !ComponentStateUtil.isUndefinedWidth(getState())) {
            StringBuilder itemHTML = new StringBuilder();
            ContextMenuItemState moreItemState = state.moreItem;
            if (moreItemState.icon != null) {
                Icon icon = getConnection()
                        .getIcon(moreItemState.icon.getURL());
                if (icon != null) {
                    itemHTML.append(icon.getElement().getString());
                }
            }

            String moreItemText = moreItemState.text;
            if (moreItemText == null || "".equals(moreItemText)) {
                moreItemText = "&#x25BA;";
            }
            itemHTML.append(moreItemText);

            contextMenu.moreItem = GWT.create(VMenuBar.CustomMenuItem.class);
            contextMenu.moreItem.setHTML(itemHTML.toString());
            contextMenu.moreItem.setCommand(VMenuBar.emptyCommand);

            contextMenu.collapsedRootItems = new VMenuBar(true, contextMenu);
            contextMenu.moreItem.setSubMenu(contextMenu.collapsedRootItems);
            contextMenu.moreItem.addStyleName(
                    contextMenu.getStylePrimaryName() + "-more-menuitem");
        }

        Stack<Iterator<ContextMenuItemState>> iteratorStack = new Stack<>();
        Stack<VMenuBar> menuStack = new Stack<>();
        VMenuBar currentMenu = contextMenuRoot.getSubMenu();
        currentMenu.clearItems();
        if (state.menuItems != null && !state.menuItems.isEmpty()) {
            Iterator<ContextMenuItemState> itr = state.menuItems.iterator();
            while (itr.hasNext()) {
                ContextMenuItemState menuItemState = itr.next();
                VMenuBar.CustomMenuItem currentItem;

                boolean itemHasCommand = menuItemState.command;
                boolean itemIsCheckable = menuItemState.checkable;

                String iconUrl = menuItemState.icon == null ? null
                        : menuItemState.icon.getURL();
                boolean subMenu = menuItemState.childItems != null
                        && !menuItemState.childItems.isEmpty();
                String itemHTML = contextMenu.buildItemHTML(
                        menuItemState.separator, subMenu, iconUrl,
                        menuItemState.text);

                Command cmd = null;
                if (!menuItemState.separator) {
                    if (itemHasCommand || itemIsCheckable) {
                        // Construct a command that fires onMenuClick(int) with
                        // the
                        // item's id-number
                        cmd = () -> contextMenu.onMenuClick(menuItemState.id);
                    }
                }

                currentItem = currentMenu.addItem(itemHTML, cmd);
                currentItem.setId("" + menuItemState.id);
                updateItemFromState(currentItem, menuItemState);

                if (subMenu) {
                    menuStack.push(currentMenu);
                    iteratorStack.push(itr);
                    itr = menuItemState.childItems.iterator();
                    currentMenu = new VContextMenu(true, currentMenu);
                    getConnection().getVTooltip()
                            .connectHandlersToWidget(currentMenu);
                    // this is the top-level style that also propagates to items
                    // -
                    // any item specific styles are set above in
                    // currentItem.updateFromUIDL(item, client)
                    if (ComponentStateUtil.hasStyles(getState())) {
                        for (String style : getState().styles) {
                            currentMenu.addStyleDependentName(style);
                        }
                    }
                    currentItem.setSubMenu(currentMenu);
                }

                while (!itr.hasNext() && !iteratorStack.empty()) {
                    boolean hasCheckableItem = false;
                    for (VMenuBar.CustomMenuItem menuItem : currentMenu
                            .getItems()) {
                        hasCheckableItem = hasCheckableItem
                                || menuItem.isCheckable();
                    }
                    if (hasCheckableItem) {
                        currentMenu.addStyleDependentName("check-column");
                    } else {
                        currentMenu.removeStyleDependentName("check-column");
                    }

                    itr = iteratorStack.pop();
                    currentMenu = menuStack.pop();
                }
            }
        }
    }

    private void updateItemFromState(VMenuBar.CustomMenuItem currentItem,
            ContextMenuItemState menuItemState) {
        currentItem.setSeparator(menuItemState.separator);
        currentItem.setEnabled(menuItemState.enabled);

        if (!menuItemState.separator && menuItemState.checked) {
            // if the selected attribute is present (either true or false),
            // the item is selectable
            currentItem.setCheckable(true);
            currentItem.setChecked(menuItemState.checked);
        } else {
            currentItem.setCheckable(false);
        }

        currentItem.setStyleName(menuItemState.styleName);

        currentItem.setDescription(menuItemState.description);
        currentItem.setDescriptionContentMode(
                menuItemState.descriptionContentMode);
        if (menuItemState.description != null) {
            currentItem.getElement().setAttribute("title",
                    menuItemState.description);
        }

        currentItem.updateStyleNames();

    }

    @Override
    protected void init() {
        super.init();
        contextMenu = GWT.create(VContextMenu.class);
        contextMenu.connector = this;
        contextMenuRoot = contextMenu.addItem("", null);
        contextMenuRoot.setSubMenu(new VContextMenu(true, contextMenu));

        registerRpc(ContextMenuClientRpc.class, new ContextMenuClientRpc() {
            @Override
            public void showContextMenu(int x, int y) {
                contextMenu.showRootMenu(x, y);
            }
        });

    }

    @Override
    public ContextMenuState getState() {
        return (ContextMenuState) super.getState();
    }

    public void onMenuClick(int clickedItemId) {
        getRpcProxy(ContextMenuServerRpc.class).itemClicked(clickedItemId);
    }

    @Override
    public Widget getWidget() {
        return contextMenuRoot.getSubMenu();
    }
}
