package com.vaadin.addon.contextmenu;

import java.util.Collections;

import com.vaadin.addon.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.ui.Grid;

public class GridWithGridListener extends Grid<String[]> {

    public GridWithGridListener() {
        setCaption("Grid with Grid specific listener");
        setHeightByRows(3);
        GridContextMenu<String[]> gridContextMenu = new GridContextMenu<>(this);

        addColumn("column 1", arr -> arr[0])
                .setCaption("Column 1(right-click here)");
        addColumn("column 2", arr -> arr[1])
                .setCaption("Column 2(right-click here)");

        ListDataProvider<String[]> dataSource = new ListDataProvider<>(
                Collections.singletonList(new String[] { "foo", "bar" }));
        setDataProvider(dataSource);
        gridContextMenu.addGridHeaderContextMenuListener(e -> {
            gridContextMenu.removeItems();
            gridContextMenu.addItem(getText(e), null);
        });

        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            gridContextMenu.addItem(getText(e), null);
        });

    }

    private static String getText(GridContextMenuOpenEvent<?> e) {

        Column<?, ?> column = e.getColumn();
        String columnText;
        if (column != null) {
            columnText = "'" + column.getCaption() + "'";
        } else {
            columnText = "'?'";
        }
        return "Context menu for " + e.getSection() + ", column: " + columnText;

    }

}
