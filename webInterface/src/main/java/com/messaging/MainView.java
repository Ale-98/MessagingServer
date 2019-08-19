package com.messaging;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import com.vaadin.flow.component.button.Button;
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

	String url="jdbc:postgresql://localhost/dbRdF";
	String usr="postgres";
	String psw="6357";
	
	private String nick = "Ale";
	private String pwd = "password";
	private Grid<User> users = new Grid<>(User.class);
	private QueryExecutor qe;
	
	public void connectToDB() {
		try {
			qe = QueryExecutor.getInstance(url, usr, psw);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public MainView() {
//		Server theServer = new Server();
    	addClassName("main-view");
    	
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        H1 header = new H1("Login as Admin");
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
//				connectToDB();
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
		HorizontalLayout main = new HorizontalLayout();
//		try {
//			users.setItems(qe.findAll());
			Collection<User> col = new ArrayList<User>();
			col.add(new User("Ale", "password"));
			users.setItems(col);
			notifyMe("Data retrieved successfully", 3000);
//		} catch (SQLException e) {
//			notifyMe("Error retieving information from DB", 3000);
//			e.printStackTrace();
//		}
		users.setSizeFull();
		main.setSizeFull();
		
		main.add(users);
		add(main);
	}
	
	// For graphical notifications
	private void notifyMe(String text, int duration) {
		Notification noty = new Notification(text);
		noty.setDuration(duration);
		noty.open();
	}
	
}
