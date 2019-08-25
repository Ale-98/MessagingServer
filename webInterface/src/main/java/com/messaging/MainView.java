package com.messaging;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
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

	String url="jdbc:postgresql://localhost/dbRdF";
	String usr="postgres";
	String psw="6357";
	
	private DataRetriever dr;
	
	private String nick = "Ale";
	private String pwd = "password";
	
	private Grid<User> users = new Grid<>(User.class);
	private TextField filter = new TextField();
	
	public void connectToDB() {
		try {
			dr = new DataRetriever(url, usr, psw);
			notifyMe("Connesso al DB postgres", 3000);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public MainView() {
//		Server theServer = new Server();
    	addClassName("main-view");
    	
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        H1 header = new H1("Admin personal area");
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
				connectToDB();
				remove(vl);
				showMainMenu();
			} else {
				notifyMe("NickName or Password invalid", 3000);
			}
		});
		
		vl.add(nickname, password, login);
		
		add(vl);
	}

	private void showMainMenu() {
		filter.setPlaceholder("filter by nickname...");
		filter.addValueChangeListener(e->updateList());
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		
		Button clearFilter = new Button();
		clearFilter.setIcon(new Icon(VaadinIcon.ERASER));
		clearFilter.addClickListener(e->filter.clear());
		
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.add(filter, clearFilter);
		
		VerticalLayout main = new VerticalLayout();
		main.setSizeFull();
		users.setSizeFull();
		
		users.setItems(dr.refreshList());
		
		main.add(toolbar, users);
		add(main);
	}
	
	private void updateList() {
		users.setItems(dr.findAll(filter.getValue()));
		notifyMe("Data retrieved successfully", 3000);
	}

	// For graphical notifications
	private void notifyMe(String text, int duration) {
		Notification.show("Data retrieved succesfully", 3000, Position.BOTTOM_START);
	}
	
}
