import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiLogin extends Application{
	
	TextField user;
	TextField password;
	Button enter;
	VBox loginBox;
	Client clientConnection;
	HashMap<String, Scene> sceneMap;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		clientConnection = new Client(data-> {
			Message msg = (Message) data;
			
			
		});
		
		clientConnection.start();
		
		user = new TextField();
		user.setPromptText("Enter Username");
		password = new TextField();
		password.setPromptText("Enter Password: password will be invisible");
		password.setStyle("-fx-text-fill: white;");
		enter = new Button("Enter");
		
		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("Login",  createGuiLogin());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


		primaryStage.setScene(sceneMap.get("Login"));
		primaryStage.setTitle("Login");
		primaryStage.show();
	}
	
	public Scene createGuiLogin() {
		loginBox = new VBox(10, user, password, enter);
		loginBox.setAlignment(Pos.TOP_CENTER);
		loginBox.setStyle("-fx-background-color: blue" + "-fx-font-family: 'serif';");
		return new Scene(loginBox, 400, 300);
	}
	
}
