import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;

public class GuiMainScreen {

    Button startGameBtn;
    Button SoloBtn;
    Button backBtn;
    VBox menuBox;
    Stage primaryStage;
    HashMap<String, Scene> sceneMap;
    Client clientConnection;

    public GuiMainScreen(Client connection, Stage primaryStage, HashMap<String, Scene> sceneMap) {
        this.clientConnection = connection;
        this.primaryStage = primaryStage;
        this.sceneMap = sceneMap;
    }

    public Scene createGuiMainScreen() {
        startGameBtn = new Button("Start Game");
        SoloBtn = new Button("Solo");
        backBtn = new Button("Back to Start");

        startGameBtn.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("board"));
        });

        SoloBtn.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("board"));
        });

        backBtn.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("start"));
        });

        menuBox = new VBox(20, startGameBtn, SoloBtn, backBtn);
        menuBox.setAlignment(Pos.CENTER);

        menuBox.setStyle("-fx-background-color: blue; -fx-font-family: 'serif';");
        return new Scene(menuBox, 400, 300);
    }
}