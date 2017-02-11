package com.vaadin.addon.contextmenu;

import java.util.EventObject;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener;
import com.vaadin.addon.contextmenu.ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

/**
 * For testing workaround for #29. Open with ../context-menu/?treetable
 */
public class ContextMenuTreeTable extends VerticalLayout {

    private ContextMenu menu;
    private TreeTable table;
    private VerticalLayout logLayout;
    private int counter;

    public ContextMenuTreeTable() {
        createTreeTable();
        logLayout = new VerticalLayout();
        Panel logPanel = new Panel();
        logPanel.setContent(logLayout);
        logPanel.setHeight("100px");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(new Button("Add ItemClickListener to TreeTable",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        addItemClickListener(table);
                    }
                }));
        buttons.addComponent(new Button("Add ContextMenu to TreeTable",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        addContextMenu(table);
                    }
                }));
        buttons.addComponent(new Button("Add ContextMenuOpenListener to Menu",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        menu.addContextMenuOpenListener(
                                new ContextMenuOpenListener() {

                                    @Override
                                    public void onContextMenuOpen(
                                            ContextMenuOpenEvent event) {
                                        log(event, "");
                                    }
                                });
                    }
                }));

        setMargin(true);
        setSpacing(true);
        addComponents(logPanel, buttons, table);
    }

    @SuppressWarnings("unchecked")
    private TreeTable createTreeTable() {
        table = new TreeTable();
        table.addContainerProperty("id", String.class, null);
        for (int i = 0; i < 10; ++i) {
            Item item = table.addItem(i);
            item.getItemProperty("id").setValue(i + " foobar");
        }
        return table;
    }

    private void addContextMenu(TreeTable table) {
        menu = new ContextMenu(table, true);
        menu.addItem("foobar", null);
        menu.addSeparator();
        menu.addItem("shazbot", null);
    }

    private void addItemClickListener(TreeTable table) {
        table.addItemClickListener(event -> {
            if (event.getButton() == MouseButton.RIGHT) {
                log(event, "context-click");
            } else if (event.isDoubleClick()) {
                log(event, "double-click");
            } else {
                log(event, "click!");
            }
        });
    }

    private void log(EventObject event, String message) {
        logLayout.addComponentAsFirst(
                new Label(counter++ + event.getClass().getSimpleName() + " "
                        + event.getSource().getClass().getSimpleName() + " "
                        + message));
    }
}
