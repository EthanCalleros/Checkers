
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
	short[][] board;
	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;
	
	GuiStart startScreen;
	GuiSignUp signupScreen;
	GuiLogin loginScreen;
	GuiBoard boardScreen;
	GuiMainScreen mainScreen;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					Message msg = (Message)data;
					if (msg.getType() == Message.messageType.id) {
						id = msg.getId();
					} else if (msg.getType() == Message.messageType.user_exists || msg.getType() == Message.messageType.bad_password) {
						signupScreen.username = "";
						signupScreen.passwordField.clear();
						signupScreen.user.clear();
						signupScreen.error.setText("Invalid username/password please input a different username and/or password");
					} else if (msg.getType() == Message.messageType.username_free && msg.getType2() == Message.messageType.ok_password){
						primaryStage.setScene(sceneMap.get("main"));
						username = signupScreen.username;
						primaryStage.setTitle("Welcome: " + username);
					} else if (msg.getType() == Message.messageType.user_dne || msg.getType() == Message.messageType.incorrect_password) {
						loginScreen.username = "";
						loginScreen.passwordField.clear();
						loginScreen.user.clear();
						loginScreen.error.setText("Invalid username/password please input a different username and/or password");
					} else if (msg.getType() == Message.messageType.correct_password && msg.getType2() == Message.messageType.user_exists) {
						username = loginScreen.username;
						primaryStage.setScene(sceneMap.get("main"));
						primaryStage.setTitle("Welcome: " + username);
					} else if (msg.getType() == Message.messageType.board) {
						if (msg.getType2() == Message.messageType.game_start) {
							boardScreen = new GuiBoard(clientConnection, primaryStage, sceneMap, msg.getIsRed(), id);
						    Scene gameScene = boardScreen.createGuiBoard();
						    sceneMap.put("board", gameScene);
						    primaryStage.setScene(gameScene);
						} else if (msg.getType2() == Message.messageType.game_move) {
							if (boardScreen != null) {
								boardScreen.Move(msg.getFromRow(), msg.getToRow(), msg.getFromCol(), msg.getToCol());
							}
						} else if (msg.getType2() == Message.messageType.game_win) {
							boardScreen.showEndGameDialog("You Win!");
						} else if (msg.getType2() == Message.messageType.game_lose) {
							boardScreen.showEndGameDialog("You Lose:(");
						} else if (msg.getType2() == Message.messageType.forfeit) {
							boardScreen.showEndGameDialog("Opponent forefeited you win!");
						}
					} else if (msg.getType() == Message.messageType.new_users) {
						if (boardScreen != null) {
							boardScreen.observer.clear();
							boardScreen.observer.add("All");
							boardScreen.observer.addAll(msg.getUsernames());
							boardScreen.options.setValue("All");
						}
					} else {
						if (boardScreen != null) {
							boardScreen.chat.getItems().add(msg.getMessage());
						}
					}
			});
		});
							
		clientConnection.start();
		
		
		sceneMap = new HashMap<String, Scene>();
		
		startScreen = new GuiStart(primaryStage, sceneMap);
		loginScreen = new GuiLogin(clientConnection, primaryStage, sceneMap, id);
		signupScreen= new GuiSignUp(clientConnection, primaryStage, sceneMap, id);
		mainScreen = new GuiMainScreen(clientConnection, primaryStage, sceneMap, username, id);
		
		sceneMap.put("start", startScreen.createGuiStart());
		sceneMap.put("login", loginScreen.createGuiLogin());
		sceneMap.put("signup", signupScreen.createGuiSignup());
		sceneMap.put("main",  mainScreen.createGuiMainScreen());
		
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

}
