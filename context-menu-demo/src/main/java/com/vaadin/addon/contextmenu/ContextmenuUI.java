package com.vaadin.addon.contextmenu;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("contextmenu")
@PreserveOnRefresh
public class ContextmenuUI extends UI {
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.addon.contextmenu.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);

		MenuBar menuBar = new MenuBar();
		fillMenu(menuBar);
		layout.addComponent(menuBar);

		final ContextMenu contextMenu = new ContextMenu(layout);
		fillMenu(contextMenu);

		button.addContextClickListener(new ContextClickListener() {
			@Override
			public void contextClick(ContextClickEvent event) {
				contextMenu.addItem("you clicked at " + event.getClientX()
						+ "x" + event.getClientY(), a -> {
					Notification.show("click");
				});
			}
		});

		// MenuItem item3 = contextMenu.addItem("root", e -> {
		// Notification.show("root"); });
		// item3.addItem("nested", e -> Notification.show("nested"));
		// MenuItem item4 = contextMenu.addItem("root2", e->
		// Notification.show("root2"));
		// item4.addItem("nested", e -> Notification.show("nested"));
		//
		// item3.setCheckable(true);
		// item3.setCheckable(true);

		button.addClickListener(e -> {
			contextMenu.addItem("dynamically added item", a -> {
				Notification.show("click");
			});
		});

		layout.addComponent(createOriginalMenu());

		Grid grid = new Grid();

		ContextMenu contextMenu2 = new ContextMenu(grid);

		grid.addColumn("Section");
		grid.addColumn("Column");
		grid.addColumn("Row");
		layout.addComponent(grid);
		grid.addContextClickListener(e -> {
			GridContextClickEvent gridE = (GridContextClickEvent) e;

			Object itemId = grid.getContainerDataSource().addItem();
			grid.getContainerDataSource().getItem(itemId)
					.getItemProperty("Section")
					.setValue(gridE.getSection().toString());
			grid.getContainerDataSource().getItem(itemId)
					.getItemProperty("Column")
					.setValue(gridE.getPropertyId().toString());
			grid.getContainerDataSource().getItem(itemId)
					.getItemProperty("Row").setValue(gridE.getRowIndex() + "");

			contextMenu2.removeItems();
			contextMenu2.addItem("Called from column " + gridE.getPropertyId()
					+ " on row " + gridE.getRowIndex(),
					f -> Notification.show("did something"));

		});
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

		MenuItem item4 = menu.addItem("Icon + Description + <b>HTML</b>",
				e -> {
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

	private Component createOriginalMenu() {
		com.vaadin.ui.MenuBar menu = new com.vaadin.ui.MenuBar();

		final com.vaadin.ui.MenuBar.MenuItem item = menu.addItem("Checkable",
				e -> {
					Notification.show("checked: " + e.isChecked());
				});
		item.setCheckable(true);
		item.setChecked(true);

		com.vaadin.ui.MenuBar.MenuItem item2 = menu.addItem("Disabled", e -> {
			Notification.show("disabled");
		});
		item2.setEnabled(false);

		com.vaadin.ui.MenuBar.MenuItem item3 = menu.addItem("Invisible", e -> {
			Notification.show("invisible");
		});
		item3.setVisible(false);

		com.vaadin.ui.MenuBar.MenuItem item4 = menu.addItem(
				"Icon + Description + <b>HTML</b>", e -> {
					Notification.show("icon");
				});
		item4.setIcon(FontAwesome.ADJUST);
		item4.setDescription("Test tooltip");

		com.vaadin.ui.MenuBar.MenuItem item5 = menu.addItem("Custom stylename",
				e -> {
					Notification.show("stylename");
				});
		item5.setStyleName("teststyle");

		com.vaadin.ui.MenuBar.MenuItem item6 = menu.addItem("Submenu", e -> {
		});
		item6.addItem("Subitem", e -> Notification.show("SubItem"));
		item6.addSeparator();
		item6.addItem("Subitem", e -> Notification.show("SubItem"))
				.setDescription("Test");

		return menu;
	}
}