package com.vaadin.addon.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.addon.contextmenu.client.ContextMenuClientRpc;
import com.vaadin.addon.contextmenu.client.ContextMenuServerRpc;
import com.vaadin.addon.contextmenu.client.MenuSharedState;
import com.vaadin.addon.contextmenu.client.MenuSharedState.MenuItemState;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.ContextClickEvent.ContextClickNotifier;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.util.ReflectTools;

@SuppressWarnings("serial")
public class ContextMenu extends AbstractExtension implements Menu {

    private AbstractMenu menu = new AbstractMenu();

    private ContextClickListener contextClickListener = new ContextClickListener() {
        @Override
        public void contextClick(ContextClickEvent event) {
            fireEvent(new ContextMenuOpenEvent(ContextMenu.this, event));

            open(event.getClientX(), event.getClientY());
        }
    };

    /**
     * @param parentComponent
     *            The component to whose lifecycle the context menu is tied to.
     * @param setAsMenuForParentComponent
     *            Determines if this menu will be shown for the parent
     *            component.
     */
    public ContextMenu(AbstractComponent parentComponent,
            boolean setAsMenuForParentComponent) {
        extend(parentComponent);

        registerRpc(new ContextMenuServerRpc() {
            @Override
            public void itemClicked(int itemId, boolean menuClosed) {
                menu.itemClicked(itemId);
            }
        });

        if (setAsMenuForParentComponent)
            setAsContextMenuOf(parentComponent);
    }

    /**
     * Sets this as a context menu of the component. You can set one menu to as
     * many components as you wish.
     * 
     * @param component
     *            the component to set the context menu to
     */
    public void setAsContextMenuOf(ContextClickNotifier component) {
        /*
         * Workaround for VScrollTable click handling, which prevents context
         * clicks from rows when ItemClickListener has been added. (#29)
         */
        if (component instanceof Table) {
            useTableSpecificContextClickListener((Table) component);
            // For context clicks outside rows (header, footer, body) we still
            // need the context click listener.
        }
        component.addContextClickListener(contextClickListener);
    }

    private void useTableSpecificContextClickListener(final Table table) {
        table.addItemClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == MouseButton.RIGHT) {
                    MouseEventDetails mouseEventDetails = new MouseEventDetails();
                    mouseEventDetails.setAltKey(event.isAltKey());
                    mouseEventDetails.setButton(event.getButton());
                    mouseEventDetails.setClientX(event.getClientX());
                    mouseEventDetails.setClientY(event.getClientY());
                    mouseEventDetails.setCtrlKey(event.isCtrlKey());
                    mouseEventDetails.setMetaKey(event.isMetaKey());
                    mouseEventDetails.setRelativeX(event.getRelativeX());
                    mouseEventDetails.setRelativeY(event.getRelativeY());
                    mouseEventDetails.setShiftKey(event.isShiftKey());
                    if (event.isDoubleClick()) {
                        mouseEventDetails.setType(0x00002);
                    } else {
                        mouseEventDetails.setType(0x00001);
                    }

                    contextClickListener.contextClick(
                            new ContextClickEvent(table, mouseEventDetails));
                }
            }
        });
    }

    public void addContextMenuOpenListener(
            ContextMenuOpenListener contextMenuComponentListener) {
        addListener(ContextMenuOpenEvent.class, contextMenuComponentListener,
                ContextMenuOpenListener.MENU_OPENED);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        // FIXME: think about where this is supposed to be

        /*
         * This should also be used by MenuBar, upgrading it from Vaadin 6 to
         * Vaadin 7 communication mechanism. Thus to be moved e.g. to the
         * AbstractMenu.
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

            if (!item.isVisible())
                continue;

            menuItemState.id = item.getId();
            menuItemState.text = item.getText();
            menuItemState.checkable = item.isCheckable();
            menuItemState.checked = item.isChecked();
            menuItemState.description = item.getDescription();
            menuItemState.enabled = item.isEnabled();
            menuItemState.separator = item.isSeparator();
            menuItemState.icon = ResourceReference.create(item.getIcon(), this,
                    "");
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

    // Should these also be in MenuInterface and then throw exception for
    // MenuBar?
    public MenuItem addSeparator() {
        // FIXME: this is a wrong way
        MenuItemImpl item = (MenuItemImpl) addItem("", null);
        item.setSeparator(true);
        return item;
    }

    public MenuItem addSeparatorBefore(MenuItem itemToAddBefore) {
        // FIXME: this is a wrong way
        MenuItemImpl item = (MenuItemImpl) addItemBefore("", null, null,
                itemToAddBefore);
        item.setSeparator(true);
        return item;
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

    /**** End of delegates to AbstractMenu ****/

    public interface ContextMenuOpenListener
            extends EventListener, Serializable {

        public static final Method MENU_OPENED = ReflectTools.findMethod(
                ContextMenuOpenListener.class, "onContextMenuOpen",
                ContextMenuOpenEvent.class);

        public void onContextMenuOpen(ContextMenuOpenEvent event);

        public static class ContextMenuOpenEvent extends EventObject {
            private final ContextMenu contextMenu;

            private final int x;
            private final int y;

            private ContextClickEvent contextClickEvent;

            public ContextMenuOpenEvent(ContextMenu contextMenu,
                    ContextClickEvent contextClickEvent) {
                super(contextClickEvent.getComponent());

                this.contextMenu = contextMenu;
                this.contextClickEvent = contextClickEvent;
                this.x = contextClickEvent.getClientX();
                this.y = contextClickEvent.getClientY();
            }

            /**
             * @return ContextMenu that was opened.
             */
            public ContextMenu getContextMenu() {
                return contextMenu;
            }

            /**
             * @return Component which initiated the context menu open request.
             */
            public Component getSourceComponent() {
                return (Component) getSource();
            }

            /**
             * @return x-coordinate of open position.
             */
            public int getX() {
                return x;
            }

            /**
             * @return y-coordinate of open position.
             */
            public int getY() {
                return y;
            }

            public ContextClickEvent getContextClickEvent() {
                return contextClickEvent;
            }
        }
    }
}
