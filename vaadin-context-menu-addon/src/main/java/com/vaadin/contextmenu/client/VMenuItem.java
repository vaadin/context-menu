package com.vaadin.contextmenu.client;

import java.util.logging.Logger;

import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VMenuBar.CustomMenuItem;

public class VMenuItem extends CustomMenuItem {

    public VMenuItem() {
        Logger.getLogger("VMenuItem").info("VMenuItem constructor");
    }

    public void setSeparator(boolean separator) {
        isSeparator = separator;
        updateStyleNames();
        if (!separator) {
            setEnabled(enabled);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void onBrowserEvent(Event event) {
        //Don't do anything. Action is handled by NativeEventPreview in ContextMenuConnector
    }
}
