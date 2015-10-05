package com.vaadin.addon.contextmenu.widgetset.client;

import java.io.Serializable;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;

// FIXME: TabIndex? MenuBar's state needs to extend a TabIndex state, ContextMenu's state doesn't need it, what's the solution?
@SuppressWarnings("serial")
public class MenuSharedState extends AbstractComponentState {
//	public class MenuBarState extends TabIndexState {
//	    {
//	        primaryStyleName = "v-menubar";
//	    }
//	}

//    public static final String ATTRIBUTE_CHECKED = "checked";
//    public static final String ATTRIBUTE_ITEM_DESCRIPTION = "description";
//    public static final String ATTRIBUTE_ITEM_ICON = "icon";
//    public static final String ATTRIBUTE_ITEM_DISABLED = "disabled";
//    public static final String ATTRIBUTE_ITEM_STYLE = "style";
	
//    public static final String HTML_CONTENT_ALLOWED = "usehtml";
//    public static final String OPEN_ROOT_MENU_ON_HOWER = "ormoh";

	public List<MenuItemState> menuItems;
//	private int numberOfItems = 0;
	public boolean htmlContentAllowed;

	public static class MenuItemState implements Serializable {
		public int id;
		public boolean separator;
		public String text;
		public boolean command;
//		public Resource icon;
		public boolean enabled;
		public boolean description;
		public boolean checkable;
		public boolean checked;
		public List<MenuItemState> childItems;
	}
}

