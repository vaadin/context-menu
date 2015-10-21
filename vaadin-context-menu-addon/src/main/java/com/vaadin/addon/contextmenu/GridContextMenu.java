package com.vaadin.addon.contextmenu;

import java.util.EventListener;

import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.addon.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridContextClickEvent;

@SuppressWarnings("serial")
public class GridContextMenu extends ContextMenu {
	
	public GridContextMenu(Grid parentComponent) {
		super(parentComponent, true);		
	}

	private void addGridSectionContextMenuListener(Section section, GridContextMenuOpenListener listener) {
		addContextMenuOpenListener(new ContextMenuOpenListener() {
			@Override
			public void onContextMenuOpen(ContextMenuOpenEvent event) {
				if (event.getContextClickEvent() instanceof GridContextClickEvent) {
					GridContextClickEvent gridEvent = (GridContextClickEvent) event.getContextClickEvent();
					if (gridEvent.getSection() == section) {
						listener.onContextMenuOpen(new GridContextMenuOpenEvent(GridContextMenu.this, gridEvent));
					}
				}
			}
		});
	}

	public void addGridHeaderContextMenuListener(GridContextMenuOpenListener listener) {
		addGridSectionContextMenuListener(Section.HEADER, listener);
	}

	public void addGridFooterContextMenuListener(GridContextMenuOpenListener listener) {
		addGridSectionContextMenuListener(Section.FOOTER, listener);
	}

	public void addGridBodyContextMenuListener(GridContextMenuOpenListener listener) {
		addGridSectionContextMenuListener(Section.BODY, listener);
	}

	public interface GridContextMenuOpenListener extends EventListener {

		public void onContextMenuOpen(GridContextMenuOpenEvent event);

		public static class GridContextMenuOpenEvent extends ContextMenuOpenEvent {
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
