
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{
	
	static String username;
	int id;
	ObservableList<String> observer;
	ComboBox<String> options;
	short[][] board;
	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;
	
	ListView<String> listItems2;
	
	GuiStart startScreen;
	GuiSignUp signupScreen;
	GuiLogin loginScreen;
	GuiMainScreen mainScreen;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		listItems2 = new ListView<String>();
		
		observer = FXCollections.observableArrayList();
		
		observer.add("All");
		
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					Message msg = (Message)data;
					if (msg.getType() == Message.messageType.id) {
						id = msg.getId();
					} else if (msg.getType() == Message.messageType.username_taken || msg.getType() == Message.messageType.bad_password) {
						signupScreen.username = "";
						signupScreen.password.clear();
						signupScreen.user.clear();
						signupScreen.error.setText("Invalid username/password please input a different username and/or password");
					} else if (msg.getType() == Message.messageType.username_free && msg.getType2() == Message.messageType.ok_password){
						primaryStage.setScene(sceneMap.get("client"));
						username = loginScreen.username;
						primaryStage.setTitle("Client: " + username);
					} else if (msg.getType() == Message.messageType.incorrect_password || msg.getType() == Message.messageType.user_dne) {
						loginScreen.username = "";
						loginScreen.password.clear();
						loginScreen.user.clear();
						loginScreen.error.setText("Invalid username/password please input a different username and/or password");
					} else if (msg.getType() == Message.messageType.correct_password && msg.getType2() == Message.messageType.user_exists) {
						
					} else if (msg.getType() == Message.messageType.board) {
						
					} else if (msg.getType() == Message.messageType.new_users) {
						observer.clear();
						observer.add("All");
						observer.addAll(msg.getUsernames());
						options.setValue("All");
					} else {
						listItems2.getItems().add(msg.getMessage());
					}
			});
		});
							
		clientConnection.start();
		
		options = new ComboBox<String>(observer);
		options.setValue("All");
		c1 = new TextField();
		b1 = new Button("Send");
		
		b1.setOnAction(e->{
			if (options.getValue().isEmpty() || options.getValue().equals("All")) {
				Message send = new Message(Message.messageType.group_message, id);
				send.setMessage(c1.getText());
				send.setReceiver("All");
				clientConnection.send(send); 
				c1.clear();
			} else {
				Message send = new Message(Message.messageType.direct_message, id);
				send.setMessage(c1.getText());
				send.setReceiver(options.getValue());
				clientConnection.send(send);
				c1.clear();
			}
			
			});
		
		sceneMap = new HashMap<String, Scene>();
		
		startScreen = new GuiStart(primaryStage, sceneMap);
		loginScreen = new GuiLogin(clientConnection, primaryStage, sceneMap, id);
		
		sceneMap.put("start", startScreen.createGuiStart());
		sceneMap.put("login", loginScreen.createGuiLogin());
		sceneMap.put("signup", signupScreen.createGuiSignup());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.setTitle("Start");
		primaryStage.show();
		
	}
	
	public Scene createClientGui() {
		
		clientBox = new VBox(10, options, c1, b1, listItems2);
		clientBox.setStyle("-fx-background-color: blue;"+"-fx-font-family: 'serif';");
		return new Scene(clientBox, 400, 300);
		
	}

}
