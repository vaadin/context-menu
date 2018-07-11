package com.vaadin.contextmenu.client;

import com.vaadin.shared.AbstractComponentState;

import java.util.List;

public class ContextMenuState extends AbstractComponentState {
    {
        primaryStyleName = "v-context-menubar";
    }

    public boolean htmlContentAllowed;

    public List<ContextMenuItemState> menuItems;

    public ContextMenuItemState moreItem;
}
