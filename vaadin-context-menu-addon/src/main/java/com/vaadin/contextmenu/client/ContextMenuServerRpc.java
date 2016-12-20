package com.vaadin.contextmenu.client;

import com.vaadin.shared.communication.ServerRpc;

public interface ContextMenuServerRpc extends ServerRpc {
    void itemClicked(int itemId, boolean menuClosed);
}
