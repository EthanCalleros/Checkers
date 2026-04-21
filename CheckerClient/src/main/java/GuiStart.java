import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuiStart{
	Button login;
	Button signUp;
	VBox options;
	
	Stage primaryStage;
	HashMap<String, Scene> sceneMap;
	
	public GuiStart(Stage primaryStage, HashMap<String, Scene> sceneMap) {
		this.primaryStage = primaryStage;
		this.sceneMap = sceneMap;
	}
	
	public Scene createGuiStart() {
		login = new Button("Login");
		signUp = new Button("Sign Up");
		login.setPrefWidth(100);
		signUp.setPrefWidth(100);
		login.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("login"));
		});
		signUp.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("signup"));
		});
		options = new VBox(10, login, signUp);
		options.setAlignment(Pos.CENTER);
		options.setStyle("-fx-background-color: blue;"+"-fx-font-family: 'serif';");
		return new Scene(options, 500, 400);
	}
}
