import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiSignUp{
	
	String username;
	int id;
	TextField user;
	TextField password;
	TextField password_again;
	Label rules;
	Label error;
	Button enter;
	VBox loginBox;
	Client clientConnection;
	Stage primaryStage;
	HashMap<String, Scene> sceneMap;
	
	public GuiSignUp(Client connection, Stage primaryStage, HashMap<String, Scene> sceneMap, int id) {
		this.clientConnection = connection;
		this.primaryStage = primaryStage;
		this.sceneMap = sceneMap;
		this.id = id;
	}
	
	public Scene createGuiSignup() {
		enter = new Button("Create");
		password = new TextField();
		password_again = new TextField();
		user = new TextField();
		rules = new Label("Password must be 6-12 characters, contain at least 1 special character, contain at least 1 number");
		error = new Label();
		loginBox = new VBox(10, user, password, password_again, enter, error);
		
		enter.setOnAction(e->{
			if (username == null || username.isEmpty()) {
				username = user.getText();
				user.clear();
				Message sendName = new Message(Message.messageType.username, id);
				sendName.setMessage(username);
				clientConnection.send(sendName);
			}
		});
		
		password.setStyle("-fx-text-fill: white");
		password_again.setStyle("-fx-text-fill: white");
		loginBox.setAlignment(Pos.TOP_CENTER);
		loginBox.setStyle("-fx-background-color: blue" + "-fx-font-family: 'serif';");
		return new Scene(loginBox, 400, 300);
	}
	
}
