package com.vaadin.addon.contextmenu;

import java.util.Collections;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener;
import com.vaadin.addon.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;

@SuppressWarnings({ "serial", "unchecked" })
@Theme("contextmenu")
public class ContextmenuUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.addon.contextmenu.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
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

        layout.addComponent(createGridWithGenericListener());
        layout.addComponent(createGridWithGridListener());
        layout.addComponent(createVaadin7Grid());
    }

    private com.vaadin.v7.ui.Grid createVaadin7Grid() {
        com.vaadin.v7.ui.Grid grid = new com.vaadin.v7.ui.Grid();
        grid.setCaption("Vaadin 7 Grid");
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(3);

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("Section", String.class, "");
        container.addContainerProperty("Column", String.class, "");
        container.addContainerProperty("Row", String.class, "");
        grid.setContainerDataSource(container);

        Item item = container.addItem("foo");
        item.getItemProperty("Section").setValue("s");
        item.getItemProperty("Column").setValue("c");
        item.getItemProperty("Row").setValue("r");

        com.vaadin.v7.addon.contextmenu.GridContextMenu contextMenu2 = new com.vaadin.v7.addon.contextmenu.GridContextMenu(
                grid);
        contextMenu2.addGridHeaderContextMenuListener(e -> {
            contextMenu2.removeItems();
            contextMenu2.addItem(
                    "Menu for header " + e.getPropertyId() + ", row "
                            + e.getRowIndex(),
                    f -> Notification.show("did something"));

        });
        contextMenu2.addGridBodyContextMenuListener(e -> {
            contextMenu2.removeItems();
            contextMenu2.addItem(
                    "Menu for column " + e.getPropertyId() + ", row "
                            + e.getRowIndex(),
                    f -> Notification.show("did something"));

        });

        return grid;
    }

    private Component createGridWithGenericListener() {
        Grid<String[]> grid = new Grid<>();
        grid.setCaption("Grid with generic listener");

        ContextMenu contextMenu2 = new ContextMenu(grid, true);
        grid.setHeightByRows(3);
        grid.addColumn("Section", arr -> arr[0]);
        grid.addColumn("Column", arr -> arr[1]);
        grid.addColumn("Row", arr -> arr[2]);

        ListDataSource<String[]> ds = new ListDataSource<>(Collections
                .singletonList(new String[] { "sec", "col", "row" }));
        grid.setDataSource(ds);
        contextMenu2.addContextMenuOpenListener(e -> {
            GridContextClickEvent<String[]> gridE = (GridContextClickEvent<String[]>) e
                    .getContextClickEvent();

            contextMenu2.removeItems();
            if (gridE.getColumn() != null) {
                contextMenu2.addItem(
                        "Called from column " + gridE.getColumn().getCaption()
                                + " on row " + gridE.getRowIndex(),
                        f -> Notification.show("did something"));
            } else if (gridE.getRowIndex() >= 0) {
                contextMenu2.addItem("Called on row " + gridE.getRowIndex(),
                        f -> Notification.show("did something"));
            } else {
                contextMenu2.addItem("Called on background",
                        f -> Notification.show("did something"));

            }

        });

        return grid;
    }

    private Component createGridWithGridListener() {
        Grid<String[]> grid = new Grid<>();
        grid.setCaption("Grid with Grid specific listener");
        grid.setHeightByRows(3);
        GridContextMenu<String[]> gridContextMenu = new GridContextMenu<>(grid);

        grid.addColumn("column 1", arr -> arr[0])
                .setCaption("Column 1(right-click here)");
        grid.addColumn("column 2", arr -> arr[1])
                .setCaption("Column 2(right-click here)");

        ListDataSource<String[]> dataSource = new ListDataSource<>(
                Collections.singletonList(new String[] { "foo", "bar" }));
        grid.setDataSource(dataSource);
        gridContextMenu.addGridHeaderContextMenuListener(e -> {
            gridContextMenu.removeItems();
            String header = getHeader(e);
            String text = "Context menu for header column: " + header;
            gridContextMenu.addItem(text, k -> {
            });
        });

        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            String text = "Context menu for column: " + getHeader(e) + ", row: "
                    + e.getRowIndex();
            gridContextMenu.addItem(text, k -> {
            });
        });

        return grid;

    }

    private String getHeader(GridContextMenuOpenEvent<?> e) {
        Column<?, ?> column = e.getColumn();
        if (column != null) {
            return "'" + column.getCaption() + "'";
        } else {
            return "'?'";
        }

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

        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).addSeparator();
        }

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