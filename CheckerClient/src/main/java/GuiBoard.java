import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GuiBoard{
	
	String username;
	String opponent;
	BorderPane root;
	BorderPane board;
	GridPane checkerBoard;
	ListView<String> chat;
	TextField chatMsg;
	Button send;
	HashMap<String, Scene> sceneMap;
	VBox chatBox;
	Button forfeit;
	
	boolean isRed;
	int[][] boardState;
	StackPane[][] squares;
	
	int selectedRow = -1;
    int selectedCol = -1;
    boolean redTurn = true;
	
	Client connection;
	
	public GuiBoard(Client clientConnection, boolean isRed) {
		root = new BorderPane();
		board = new BorderPane();
		checkerBoard = new GridPane();
		boardState = new int[8][8];
		squares = new StackPane[8][8];
		this.connection = clientConnection;
		this.isRed = isRed;
	}
	
	private void boardInit() {
		for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) boardState[row][col] = 1;
                    else if (row > 4) boardState[row][col] = 2;
                    else boardState[row][col] = 0;
                }
            }
        }
	}
	
	private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        int piece = boardState[fromRow][fromCol];

        if (boardState[toRow][toCol] != 0) return false;
        if ((toRow + toCol) % 2 == 0) return false;

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        if (colDiff == 1) {
            if (piece == 1 && rowDiff == 1) return true;
            if (piece == 2 && rowDiff == -1) return true;
            if ((piece == 3 || piece == 4) && Math.abs(rowDiff) == 1) return true;
        }

        if (colDiff == 2 && Math.abs(rowDiff) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            int midPiece = boardState[midRow][midCol];
            
            if ((piece == 1 || piece == 3) && (midPiece == 2 || midPiece == 4)) {
                if (piece == 1 && rowDiff == 2) return true;
                if (piece == 3 && Math.abs(rowDiff) == 2) return true;
            }
            if ((piece == 2 || piece == 4) && (midPiece == 1 || midPiece == 3)) {
                if (piece == 2 && rowDiff == -2) return true;
                if (piece == 4 && Math.abs(rowDiff) == 2) return true;
            }
        }

        return false;
    }
	
	private void handlePiece(int row, int col) {
		if (redTurn && !isRed) return;
        if (!redTurn && isRed) return;

        int piece = boardState[row][col];

        if (selectedRow == -1) {
            if (redTurn && (piece == 1 || piece == 3)) {
                selectedRow = row;
                selectedCol = col;
                squares[row][col].setStyle("-fx-border-color: yellow; -fx-border-width: 3;");
            } else if (!redTurn && (piece == 2 || piece == 4)) {
                selectedRow = row;
                selectedCol = col;
                squares[row][col].setStyle("-fx-border-color: yellow; -fx-border-width: 3;");
            }
        } else {
        	if (isValidMove(selectedRow, selectedCol, row, col)) {

                Message msg = new Message(Message.messageType.game_move, 0);
                msg.setMove(selectedRow, selectedCol, row, col);
                connection.send(msg);

                movePiece(selectedRow, selectedCol, row, col);
                redTurn = !redTurn;
            }
            squares[selectedRow][selectedCol].setStyle("");
            selectedRow = -1;
            selectedCol = -1;
        }
	}
	
	private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        int piece = boardState[fromRow][fromCol];

        if (Math.abs(toRow - fromRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            boardState[midRow][midCol] = 0;
        }

        boardState[toRow][toCol] = piece;
        boardState[fromRow][fromCol] = 0;

        if (piece == 1 && toRow == 7) boardState[toRow][toCol] = 3;
        if (piece == 2 && toRow == 0) boardState[toRow][toCol] = 4;

        visualBoard();
    }
	
	private void visualBoard() {
		checkerBoard.getChildren().clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(60, 60);
                squares[row][col] = square;

                Rectangle bg = new Rectangle(60, 60);
                bg.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                square.getChildren().add(bg);

                int piece = boardState[row][col];
                if (piece != 0) {
                    Circle circle = new Circle(20);
                    if (piece == 1) circle.setFill(Color.RED);
                    else if (piece == 2) circle.setFill(Color.BLACK);
                    else if (piece == 3) circle.setFill(Color.DARKRED);
                    else if (piece == 4) circle.setFill(Color.DARKGRAY);
                    square.getChildren().add(circle);
                    
                    if (piece == 3 || piece == 4) {
                        Text crown = new Text("♛");
                        crown.setFill(Color.GOLD);
                        square.getChildren().add(crown);
                    }
                }
                
                final int r = row;
                final int c = col;
                square.setOnMouseClicked(e -> handlePiece(r, c));
                checkerBoard.add(square, col, row);
            }
        }
	}
	
	public void Move(int fromRow, int toRow, int fromCol, int toCol) {
		Platform.runLater(() -> {
            movePiece(fromRow, fromCol, toRow, toCol);
            redTurn = !redTurn;
        });
	}
	
	public void Chat(String msg) {
		Platform.runLater(() -> {
            chat.getItems().add(msg);
        });
	}
	
	public Scene createGuiBoard() {
		
		boardInit();
	    visualBoard();

	    board.setCenter(checkerBoard);
	    root.setCenter(board);

	    // Initialize chat components
	    chat = new ListView<>();
	    chatMsg = new TextField();
	    chatMsg.setPromptText("Enter message...");
	    send = new Button("Send");
	    forfeit = new Button("Forfeit");

	    send.setOnAction(e -> {
	        if (!chatMsg.getText().isEmpty()) {
	            Message msg = new Message(Message.messageType.group_message, 0);
	            msg.setMessage(username + ": " + chatMsg.getText());
	            msg.setReceiver("All");
	            connection.send(msg);
	            chatMsg.clear();
	        }
	    });

	    chatBox = new VBox(10, chat, chatMsg, send);
	    chatBox.setPrefWidth(200);
	    root.setRight(chatBox);

	    return new Scene(root, 800, 500);
    }
}
