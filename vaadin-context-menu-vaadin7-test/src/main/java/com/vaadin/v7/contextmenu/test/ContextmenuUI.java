package com.vaadin.v7.contextmenu.test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.contextmenu.GridContextMenu;
import com.vaadin.v7.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

@Theme("contextmenu")
public class ContextmenuUI extends UI {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String SHOE_SIZE = "Shoe size";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        if (request.getParameter("treetable") != null) {
            setContent(new ContextMenuTreeTable());
        } else {
            final VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            setContent(layout);

            layout.addComponent(createVaadin7Grid());
        }

    }

    private Grid createVaadin7Grid() {
        Grid grid = new Grid();
        grid.setCaption("Vaadin 7 Grid");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(3);

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(FIRST_NAME, String.class, "");
        container.addContainerProperty(LAST_NAME, String.class, "");
        container.addContainerProperty(SHOE_SIZE, String.class, "");
        grid.setContainerDataSource(container);

        Item item = container.addItem("foo");
        item.getItemProperty(FIRST_NAME).setValue("John");
        item.getItemProperty(LAST_NAME).setValue("Doe");
        item.getItemProperty(SHOE_SIZE).setValue("48");

        GridContextMenu contextMenu2 = new GridContextMenu(grid);
        contextMenu2.addGridHeaderContextMenuListener(e -> {
            contextMenu2.removeItems();
            contextMenu2.addItem(getInfo(e),
                    f -> Notification.show("did something"));

        });
        contextMenu2.addGridBodyContextMenuListener(e -> {
            contextMenu2.removeItems();
            contextMenu2.addItem(getInfo(e),
                    f -> Notification.show("did something"));

        });

        return grid;
    }

    private String getInfo(GridContextMenuOpenEvent e) {
        return "Menu for " + e.getSection() + ", column: " + e.getPropertyId()
                + ", row: " + e.getRowIndex();
    }

}
