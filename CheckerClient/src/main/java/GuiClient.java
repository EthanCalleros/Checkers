
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

public class GuiClient extends Application{

	
	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;
	
	ListView<String> listItems2;
	ListView<String> userList;
	
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		userList = new ListView<>();
		clientConnection = new Client(data->{
				Platform.runLater(()->{
					Message msg = (Message) data;

					if (msg.type == 1) {
						listItems2.getItems().add("Server: Welcome " + msg.sender + "!");
						clientConnection.userName = msg.sender;
					}
					else if (msg.type == 4) {
						listItems2.getItems().add("error: " + msg.messageText);
					} else if (msg.type == 5) {
						userList.getItems().clear();
						userList.getItems().addAll(msg.recipients);
					} else {
						String prefix = (msg.type == 3) ? "[Private]" : "";
						listItems2.getItems().add(msg.sender + ": " + msg.messageText);
					}
			});
		});
							
		clientConnection.start();

		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{
			Message msg = new Message();
			String target = userList.getSelectionModel().getSelectedItem();

			if (clientConnection.userName == null) {
				msg.type = 1;
				msg.sender = c1.getText();
			} else if (target != null && target.equals("All") == false) {
				msg.type = 3;
				msg.sender = clientConnection.userName;
				msg.messageText = c1.getText();
				msg.recipients.add(target);
			} else {
				msg.type = 2;
				msg.sender = clientConnection.userName;
				msg.messageText = c1.getText();
			}
			clientConnection.send(msg);
			c1.clear();
		});
		
		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


		primaryStage.setScene(sceneMap.get("client"));
		primaryStage.setTitle("Client");
		primaryStage.show();
		
	}
	

	
	public Scene createClientGui() {
		HBox lists = new HBox(10, listItems2, userList);
		clientBox = new VBox(10, c1,b1,lists);
		clientBox.setStyle("-fx-background-color: blue;"+"-fx-font-family: 'serif';");
		return new Scene(clientBox, 400, 300);
		
	}

}
