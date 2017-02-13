package com.vaadin.addon.contextmenu;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "unchecked" })
@Theme("contextmenu")
// @PreserveOnRefresh
public class ContextmenuUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.addon.contextmenu.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        if (request.getParameter("treetable") != null) {
            setContent(new ContextMenuTreeTable());
        } else {
            initUI();
        }
    }

    private void initUI() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Button 1");
        layout.addComponent(button);

        Button button2 = new Button("Button 2");
        layout.addComponent(button2);

        ContextMenu contextMenu = new ContextMenu(this, false);
        fillMenu(contextMenu);

        contextMenu.setAsContextMenuOf(button);
        contextMenu.setAsContextMenuOf(button2);

        contextMenu.addContextMenuOpenListener(new ContextMenuOpenListener() {
            @Override
            public void onContextMenuOpen(ContextMenuOpenEvent event) {
                Notification.show("Context menu on"
                        + event.getSourceComponent().getCaption());
            }
        });

        layout.addComponent(createGrid1());
        layout.addComponent(createGrid2());
    }

    private Component createGrid1() {
        Grid grid = new Grid();

        ContextMenu contextMenu2 = new ContextMenu(grid, true);

        grid.addColumn("Section");
        grid.addColumn("Column");
        grid.addColumn("Row");

        contextMenu2.addContextMenuOpenListener(e -> {
            GridContextClickEvent gridE = (GridContextClickEvent) e
                    .getContextClickEvent();

            Object itemId = grid.getContainerDataSource().addItem();
            Item item = grid.getContainerDataSource().getItem(itemId);
            item.getItemProperty("Section")
                    .setValue(String.valueOf(gridE.getSection()));
            Object propertyId = gridE.getPropertyId();

            item.getItemProperty("Column").setValue(
                    propertyId != null ? propertyId.toString() : "??");
            int rowIndex = gridE.getRowIndex();
            item.getItemProperty("Row").setValue(
                    rowIndex < 0 ? "Outside rows" : String.valueOf(rowIndex));

            contextMenu2.removeItems();
            contextMenu2.addItem(
                    "Called from column " + propertyId + " on row "
                            + gridE.getRowIndex(),
                    f -> Notification.show("did something"));

        });

        return grid;
    }

    private Component createGrid2() {
        Grid grid = new Grid();

        GridContextMenu gridContextMenu = new GridContextMenu(grid);

        grid.addColumn("column 1")
                .setHeaderCaption("Column 1(right-click here)");
        grid.addColumn("column 2")
                .setHeaderCaption("Column 2(right-click here)");
        ;

        gridContextMenu.addGridHeaderContextMenuListener(e -> {
            gridContextMenu.removeItems();
            gridContextMenu.addItem("Add Item", k -> {
                Object itemId = grid.getContainerDataSource().addItem();
                grid.getContainerDataSource().getItem(itemId)
                        .getItemProperty("column 1")
                        .setValue("added from header column "
                                + e.getPropertyId());
            });
        });

        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Add Item", k -> {
                    Object newItemId = grid.getContainerDataSource().addItem();
                    grid.getContainerDataSource().getItem(newItemId)
                            .getItemProperty("column 1")
                            .setValue("added from empty space");
                });
            } else {
                gridContextMenu.addItem("Remove this row", k -> {
                    grid.getContainerDataSource().removeItem(itemId);
                });
            }
        });

        return grid;
    }

    private void fillMenu(Menu menu) {
        final MenuItem item = menu.addItem("Checkable", e -> {
            Notification.show("checked: " + e.isChecked());
        });
        item.setCheckable(true);
        item.setChecked(true);

        MenuItem item2 = menu.addItem("Disabled", e -> {
            Notification.show("disabled");
        });
        item2.setEnabled(false);

        MenuItem item3 = menu.addItem("Invisible", e -> {
            Notification.show("invisible");
        });
        item3.setVisible(false);

        if (menu instanceof ContextMenu)
            ((ContextMenu) menu).addSeparator();

        MenuItem item4 = menu.addItem("Icon + Description + <b>HTML</b>", e -> {
            Notification.show("icon");
        });
        item4.setIcon(FontAwesome.ADJUST);
        item4.setDescription("Test tooltip");

        MenuItem item5 = menu.addItem("Custom stylename", e -> {
            Notification.show("stylename");
        });
        item5.setStyleName("teststyle");

        MenuItem item6 = menu.addItem("Submenu", e -> {
        });
        item6.addItem("Subitem", e -> Notification.show("SubItem"));
        item6.addSeparator();
        item6.addItem("Subitem", e -> Notification.show("SubItem"))
                .setDescription("Test");
    }
}