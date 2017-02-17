package com.vaadin.v7.contextmenu;

import java.io.Serializable;
import java.util.EventListener;

import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.contextmenu.ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.v7.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.v7.shared.ui.grid.GridConstants.Section;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.GridContextClickEvent;

@SuppressWarnings("serial")
/**
 * Compatibility version of ContextMenu to use with v7.Grid in the Framework 8.0
 * compatibility packages.
 *
 * @deprecated To use only for compatibility v7.Grid
 */
@Deprecated
public class GridContextMenu extends ContextMenu {

    public GridContextMenu(Grid parentComponent) {
        super(parentComponent, true);
    }

    private void addGridSectionContextMenuListener(final Section section,
            final GridContextMenuOpenListener listener) {
        addContextMenuOpenListener(new ContextMenuOpenListener() {
            @Override
            public void onContextMenuOpen(ContextMenuOpenEvent event) {
                if (event
                        .getContextClickEvent() instanceof GridContextClickEvent) {
                    GridContextClickEvent gridEvent = (GridContextClickEvent) event
                            .getContextClickEvent();
                    if (gridEvent.getSection() == section) {
                        listener.onContextMenuOpen(new GridContextMenuOpenEvent(
                                GridContextMenu.this, gridEvent));
                    }
                }
            }
        });
    }

    public void addGridHeaderContextMenuListener(
            GridContextMenuOpenListener listener) {
        addGridSectionContextMenuListener(Section.HEADER, listener);
    }

    public void addGridFooterContextMenuListener(
            GridContextMenuOpenListener listener) {
        addGridSectionContextMenuListener(Section.FOOTER, listener);
    }

    public void addGridBodyContextMenuListener(
            GridContextMenuOpenListener listener) {
        addGridSectionContextMenuListener(Section.BODY, listener);
    }

    public interface GridContextMenuOpenListener
            extends EventListener, Serializable {

        public void onContextMenuOpen(GridContextMenuOpenEvent event);

        public static class GridContextMenuOpenEvent
                extends ContextMenuOpenEvent {
            private final Object itemId;
            private final int rowIndex;
            private final Object propertyId;
            private final Section section;

            public GridContextMenuOpenEvent(ContextMenu contextMenu,
                    GridContextClickEvent contextClickEvent) {
                super(contextMenu, contextClickEvent);
                itemId = contextClickEvent.getItemId();
                rowIndex = contextClickEvent.getRowIndex();
                propertyId = contextClickEvent.getPropertyId();
                section = contextClickEvent.getSection();
            }

            public Object getItemId() {
                return itemId;
            }

            public int getRowIndex() {
                return rowIndex;
            }

            public Object getPropertyId() {
                return propertyId;
            }

            public Section getSection() {
                return section;
            }
        }
    }
}
