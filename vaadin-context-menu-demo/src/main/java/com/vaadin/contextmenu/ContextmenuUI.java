package com.vaadin.contextmenu;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.contextmenu.ContextMenu.ContextMenuOpenListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "unchecked" })
@Theme("contextmenu")
public class ContextmenuUI extends UI {
    Button but3=new Button("remove Tooltip");

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.contextmenu.demo.DemoWidgetSet")
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

        layout.addComponent(new GridWithGenericListener());
        layout.addComponent(new GridWithGridListener());
        layout.addComponent(but3);
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
        item2.setDescription("Disabled item");
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
        item4.setIcon(VaadinIcons.ADJUST);
        item4.setDescription("Test tooltip");
        but3.addClickListener(e->item4.setDescription(""));
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