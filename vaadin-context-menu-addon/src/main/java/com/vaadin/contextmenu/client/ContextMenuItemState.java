package com.vaadin.contextmenu.client;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.ContentMode;

import java.io.Serializable;
import java.util.List;

public class ContextMenuItemState implements Serializable {
    public int id;
    public boolean separator;
    public String text;
    public boolean command;
    public URLReference icon;
    public boolean enabled;
    @NoLayout
    public String description;
    @NoLayout
    public ContentMode descriptionContentMode = ContentMode.PREFORMATTED;
    public boolean checkable;
    public boolean checked;
    public List<ContextMenuItemState> childItems;
    public String styleName;

}
