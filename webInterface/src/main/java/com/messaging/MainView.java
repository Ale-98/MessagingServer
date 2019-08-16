package com.messaging;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.router.Route;

/**
 * The main view contains a button and a click listener.
 */
@Route
@PWA(name = "My Application", shortName = "My Application")
@StyleSheet("frontend://styles/style.css")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private String nick = "Ale";
	private String pwd = "password";
	
	public MainView() {
//		Server theServer = new Server();
    	addClassName("main-view");
    	
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        H1 header = new H1("Login as Adimin");
        header.getElement().getThemeList().add("dark");
        
        add(header);
        
        askForCredentials();
    }

	private void askForCredentials() {
		VerticalLayout vl = new VerticalLayout();
		TextField nickname = new TextField("Nickname");
		PasswordField password = new PasswordField("Password");
		Button login = new Button("Login");
		
		login.addClickListener(click->{
			if(nickname.getValue().equals(nick)&&password.getValue().equals(pwd)) {
				remove(vl);
				showMainMenu();
			} else {
				Notification badCredentials = new Notification("nickname or passord invalid");
				badCredentials.setDuration(3000);
				badCredentials.open();
			}
		});
		
		vl.add(nickname, password, login);
		
		add(vl);
	}

	private void showMainMenu() {
		HorizontalLayout main = new HorizontalLayout();
		Grid<String> users = new Grid<String>();
		GridContextMenu<String> cm = users.addContextMenu();
		
		main.add(users);
	}
}
