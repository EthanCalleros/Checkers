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

public class GuiLogin{
	
	String username;
	int id;
	TextField user;
	TextField password;
	Label error;
	Button enter;
	VBox loginBox;
	Client clientConnection;
	Stage primaryStage;
	HashMap<String, Scene> sceneMap;
	
	public GuiLogin(Client connection, Stage primaryStage, HashMap<String, Scene> sceneMap, int id) {
		this.clientConnection = connection;
		this.primaryStage = primaryStage;
		this.sceneMap = sceneMap;
		this.id = id;
	}
	
	public Scene createGuiLogin() {
		enter = new Button("Join");
		password = new TextField();
		user = new TextField();
		error = new Label();
		loginBox = new VBox(10, user, password, enter, error);
		
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
		loginBox.setAlignment(Pos.TOP_CENTER);
		loginBox.setStyle("-fx-background-color: blue" + "-fx-font-family: 'serif';");
		return new Scene(loginBox, 400, 300);
	}
	
}
