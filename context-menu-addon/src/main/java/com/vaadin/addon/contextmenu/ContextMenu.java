package com.vaadin.addon.contextmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.addon.contextmenu.widgetset.client.ContextMenuClientRpc;
import com.vaadin.addon.contextmenu.widgetset.client.ContextMenuServerRpc;
import com.vaadin.addon.contextmenu.widgetset.client.MenuSharedState;
import com.vaadin.addon.contextmenu.widgetset.client.MenuSharedState.MenuItemState;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.ui.AbstractComponent;

@SuppressWarnings("serial")
public class ContextMenu extends AbstractExtension implements Menu {
	
	private Logger logger = Logger.getLogger("ContextMenu");
	
    private AbstractMenu menu = new AbstractMenu();

    public ContextMenu(AbstractComponent component) {
    	extend(component);
    	
		registerRpc(new ContextMenuServerRpc() {
			@Override
			public void itemClicked(int itemId, boolean menuClosed) {
				menu.itemClicked(itemId);
			}
		});

    	component.addContextClickListener(new ContextClickListener() {
			@Override
			public void contextClick(ContextClickEvent event) {
				logger.info("Context click listener");
				open(event.getClientX(), event.getClientY());
			}
		});
	}

    @Override
    public void beforeClientResponse(boolean initial) {
    	logger.info("beforeClientResponse");
    	
    	super.beforeClientResponse(initial);

    	// FIXME: think about where this is supposed to be
    	
        /*
         * This should also be used by MenuBar, upgrading it from Vaadin 6 to Vaadin 7 communication mechanism.
         * Thus to be moved e.g. to the AbstractMenu.
         */
    	MenuSharedState menuSharedState = getState();
    	menuSharedState.htmlContentAllowed = isHtmlContentAllowed();
    	menuSharedState.menuItems = convertItemsToState(getItems());
    }
    
    public void open(int x, int y) {
		getRpcProxy(ContextMenuClientRpc.class).showContextMenu(x, y);
    }
    
    private List<MenuItemState> convertItemsToState(List<MenuItem> items) {
    	if (items == null || items.size() == 0)
    		return null;
    	
    	List<MenuItemState> state = new ArrayList<MenuItemState>();
    	
    	for (MenuItem item : items) {
    		MenuItemState menuItemState = new MenuItemState();
    		menuItemState.id = item.getId();
    		menuItemState.text = item.getText();
    		menuItemState.checkable = item.isCheckable();
			menuItemState.checked = item.isChecked();
			menuItemState.description = item.getDescription();
			menuItemState.enabled = item.isEnabled();
			menuItemState.separator = item.isSeparator();
			menuItemState.icon = ResourceReference.create(
	                item.getIcon(), this, "");
			menuItemState.styleName = item.getStyleName();
    		
    		menuItemState.childItems = convertItemsToState(item.getChildren());
    		
    		state.add(menuItemState);
    	}
    	
    	return state;
    }
    
    @Override
    protected MenuSharedState getState() {
    	return (MenuSharedState) super.getState();
    }
        
    /**** Delegates to AbstractMenu ****/
    
	@Override
	public MenuItem addItem(String caption, Command command) {
		return menu.addItem(caption, command);
	}

	@Override
	public MenuItem addItem(String caption, Resource icon, Command command) {
		return menu.addItem(caption, icon, command);
	}

	@Override
	public MenuItem addItemBefore(String caption, Resource icon,
			Command command, MenuItem itemToAddBefore) {
		return menu.addItemBefore(caption, icon, command, itemToAddBefore);
	}

	@Override
	public List<MenuItem> getItems() {
		return menu.getItems();
	}

	@Override
	public void removeItem(MenuItem item) {
		menu.removeItem(item);
	}

	@Override
	public void removeItems() {
		menu.removeItems();
	}

	@Override
	public int getSize() {
		return menu.getSize();
	}

	@Override
	public void setHtmlContentAllowed(boolean htmlContentAllowed) {
		menu.setHtmlContentAllowed(htmlContentAllowed);
	}

	@Override
	public boolean isHtmlContentAllowed() {
		return menu.isHtmlContentAllowed();
	}

    /**** End of deletates to AbstractMenu ****/
}
