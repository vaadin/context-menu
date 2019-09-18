package com.vaadin.contextmenu;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.contextmenu.ContextMenu.ContextMenuOpenListener;
import com.vaadin.data.TreeData;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({"serial", "unchecked"})
@Theme("contextmenu")
public class ContextMenuUI extends UI {
    private Button but3 = new Button("remove Tooltip");

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextMenuUI.class)
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

        contextMenu.addContextMenuOpenListener(
                (ContextMenuOpenListener) event ->
                        Notification.show("Context menu on" + event.getSourceComponent().getCaption()));
        contextMenu.setHtmlContentAllowed(true);
        layout.addComponent(new GridWithGenericListener());
        layout.addComponent(new GridWithGridListener());
        layout.addComponent(but3);
        layout.addComponent(new Button("Remove items from context menu", e -> contextMenu.removeItems()));
        addTree(layout);
    }

    private void addTree(VerticalLayout layout) {
        TreeData<String> data = new TreeData<>();
        data.addItem(null, "Dad");
        data.addItem("Dad", "Daughter");
        data.addItem("Daughter", "Granddaughter");
        data.addItem("Dad", "Son");
        data.addItem("Daughter", "Grandson");

        Tree<String> tree = new Tree<>("A family", data);
        TreeContextMenu<String> treeContextMenu = new TreeContextMenu<>(tree);
        treeContextMenu.addTreeContextMenuListener(e -> {
            treeContextMenu.removeItems();
            //The path is /resources/images/kitten.jpg
            ClassResource ico = new ClassResource("/images/kitten.jpg");
            treeContextMenu.addItem("Who?",ico, menuItem -> Notification.show(e.getItem()));
        });

        layout.addComponent(tree);
    }

    private void fillMenu(ContextMenu menu) {
        final MenuItem item = menu.addItem("Checkable \u00d6",
                e -> Notification.show("checked: " + e.isChecked())
        );
        item.setCheckable(true);
        item.setChecked(true);

        MenuItem item2 = menu.addItem("Disabled",
                e -> Notification.show("disabled")
        );
        item2.setDescription("Disabled item");
        item2.setEnabled(false);

        MenuItem item3 = menu.addItem("Invisible",
                e -> Notification.show("invisible")
        );
        item3.setVisible(false);

        menu.addSeparator();

        //The path is /resources/images/kitten.jpg
        ClassResource ico = new ClassResource("/images/kitten.jpg");
        MenuItem item4 = menu.addItem("Icon + Description + <b>HTML</b>",ico,
                e -> Notification.show("icon")
        );
        item4.setDescription("Test tooltip");
        but3.addClickListener(e -> item4.setDescription(""));
        MenuItem item5 = menu.addItem("Custom stylename",
                e -> Notification.show("stylename")
        );
        //The path is /webapp/VAADIN/themes/contextmenu/images
        ThemeResource resource = new ThemeResource("images/cat.jpg");
        item5.setIcon(resource);

        item5.setStyleName("teststyle");

        MenuItem item6 = menu.addItem("Submenu");
        item6.addItem("Subitem", VaadinIcons.OPTION, e -> Notification.show("SubItem"));
        item6.addSeparator();
        item6.addItem("Subitem", e -> Notification.show("SubItem"))
                .setDescription("Test");
        MenuItem openWindowNotification = item6.addItem(
                "Open Window + Notification",
                e -> Notification.show("Open Vaadin.com"));
        new BrowserWindowOpener("https://vaadin.com").extend(openWindowNotification);

        MenuItem openWindowDummy = item6.addItem("Open Google");
        new BrowserWindowOpener("https://google.com").extend(openWindowDummy);

        new BrowserWindowOpener("https://yahoo.com")
                .extend(item6.addItem("SubMenu2").addItem("Yahoo!"));
    }
}
