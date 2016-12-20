package com.vaadin.contextmenu;

import java.io.Serializable;
import java.util.EventListener;

import com.vaadin.contextmenu.ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.GridContextClickEvent;

@SuppressWarnings("serial")
public class GridContextMenu<T> extends ContextMenu {

    public GridContextMenu(Grid<T> parentComponent) {
        super(parentComponent, true);
    }

    private void addGridSectionContextMenuListener(final Section section,
            final GridContextMenuOpenListener<T> listener) {
        addContextMenuOpenListener(new ContextMenuOpenListener() {
            @Override
            public void onContextMenuOpen(ContextMenuOpenEvent event) {
                if (event
                        .getContextClickEvent() instanceof GridContextClickEvent) {
                    @SuppressWarnings("unchecked")
                    GridContextClickEvent<T> gridEvent = (GridContextClickEvent<T>) event
                            .getContextClickEvent();
                    if (gridEvent.getSection() == section) {
                        listener.onContextMenuOpen(
                                new GridContextMenuOpenEvent<T>(
                                        GridContextMenu.this, gridEvent));
                    }
                }
            }
        });
    }

    public void addGridHeaderContextMenuListener(
            GridContextMenuOpenListener<T> listener) {
        addGridSectionContextMenuListener(Section.HEADER, listener);
    }

    public void addGridFooterContextMenuListener(
            GridContextMenuOpenListener<T> listener) {
        addGridSectionContextMenuListener(Section.FOOTER, listener);
    }

    public void addGridBodyContextMenuListener(
            GridContextMenuOpenListener<T> listener) {
        addGridSectionContextMenuListener(Section.BODY, listener);
    }

    public interface GridContextMenuOpenListener<T>
            extends EventListener, Serializable {

        public void onContextMenuOpen(GridContextMenuOpenEvent<T> event);

        public static class GridContextMenuOpenEvent<T>
                extends ContextMenuOpenEvent {
            private final Object item;
            private final int rowIndex;
            private final Column<?, ?> column;
            private final Section section;

            public GridContextMenuOpenEvent(ContextMenu contextMenu,
                    GridContextClickEvent<T> contextClickEvent) {
                super(contextMenu, contextClickEvent);
                item = contextClickEvent.getItem();
                rowIndex = contextClickEvent.getRowIndex();
                column = contextClickEvent.getColumn();
                section = contextClickEvent.getSection();
            }

            public Object getItem() {
                return item;
            }

            public int getRowIndex() {
                return rowIndex;
            }

            public Column<?, ?> getColumn() {
                return column;
            }

            public Section getSection() {
                return section;
            }
        }
    }
}
