package com.vaadin.addon.contextmenu.widgetset.client;

import java.util.logging.Logger;

import com.vaadin.client.ui.VMenuBar.CustomMenuItem;

public class VMenuItem extends CustomMenuItem {

	public VMenuItem() {
		Logger.getLogger("VMenuItem").info("VMenuItem constructor");
	}
	
    public void setSeparator(boolean separator) {
		Logger.getLogger("VMenuItem").info("set separator: "+separator);
    	
        isSeparator = separator;
        updateStyleNames();
        if (!separator) {
            setEnabled(enabled);
        }
    }

	public void setDescription(String description) {
		this.description = description;
	}
}
