

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("contextmenu")
@Widgetset("com.vaadin.addon.contextmenu.demo.DemoWidgetSet")
public class ContextmenuUI extends UI {

//	@WebServlet(value = "/*", asyncSupported = true)
//	@VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.addon.contextmenu.widgetset.ContextmenuWidgetset")
//	public static class Servlet extends VaadinServlet {
//	}

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
		
//		com.vaadin.ui.MenuBar menuBar = new com.vaadin.ui.MenuBar();
//		MenuItem item = menuBar.addItem("test", e -> {});
//		MenuItem item2 = menuBar.addItem("test2", e -> {});
//		item.addItem("popup", e -> Notification.show("popup"));
//		item.addItem("popup2", e -> Notification.show("popup"));
//
//		layout.addComponent(menuBar);
		
		ContextMenu contextMenu = new ContextMenu(button);
//		MenuItem item3 = contextMenu.addItem("root", e -> { Notification.show("root"); });
//		item3.addItem("nested", e -> Notification.show("nested"));
//		MenuItem item4 = contextMenu.addItem("root2", e-> Notification.show("root2"));
//		item4.addItem("nested", e -> Notification.show("nested"));
//		
//		button.addClickListener(e -> {
//			contextMenu.addItem("dynamically added item", a -> { Notification.show("click"); });
//		});
	}	
}