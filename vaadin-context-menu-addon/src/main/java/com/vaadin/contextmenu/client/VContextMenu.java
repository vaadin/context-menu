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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.client.ui.VOverlay;

public class VContextMenu extends VMenuBar {

    public ContextMenuConnector connector;

    private static VContextMenu currentVisibleContextMenu;

    public VContextMenu(boolean subMenu, VMenuBar parentMenu) {
        super(subMenu, parentMenu);
    }

    public VContextMenu() {
    }

    public void showRootMenuAt(CustomMenuItem item, int top, int left) {
        super.showChildMenuAt(item, top, left);
        currentVisibleContextMenu = this;
    }

    @Override
    public void onClose(CloseEvent<PopupPanel> event) {
        super.close(event, currentVisibleContextMenu == null);
        currentVisibleContextMenu = null;
    }

    @Override
    protected VOverlay createOverlay() {
        VOverlay overlay = super.createOverlay();
        for (VMenuBar current = this; current != null; current = current
                .getParentMenu()) {
            if (current.client != null) {
                overlay.setApplicationConnection(current.client);
                break;
            }
        }
        return overlay;
    }

    @Override
    public boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (keycode == KeyCodes.KEY_TAB) {
            return true;
        }
        if (keycode == getNavigationLeftKey() && (getParentMenu() == null
                || getParentMenu().getParentMenu() == null)) {
            // do not close parent menu by left key
            return true;
        }
        if (keycode == getNavigationRightKey() && getSelected() != null
                && getSelected().getSubMenu() == null) {
            // do not close menu by right key if there is no submenu
            return true;
        }
        return super.handleNavigation(keycode, ctrl, shift);
    }

    @Override
    public void onMenuClick(int clickedItemId) {
        connector.onMenuClick(clickedItemId);
    }

    @Override
    protected VMenuBar getRoot() {
        VMenuBar root = this;

        while (root.getParentMenu() != null
                && root.getParentMenu().getParentMenu() != null) {
            root = root.getParentMenu();
        }

        return root;
    }
}
