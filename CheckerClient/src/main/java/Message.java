import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    
    public enum messageType {
    	id,
    	username,
    	username_taken,
    	username_free,
    	bad_password,
    	ok_password,
    	correct_password,
    	incorrect_password,
    	user_exists,
    	user_dne,
    	group_message,
    	direct_message,
    	receive_message,
    	user_joined,
    	user_left,
    	board,
    	new_users
    }
    
    private int id;
    private messageType type;
    private messageType type2;
    private String sender;
    private String receiver;
    private String message;
    private ArrayList<String> usernames;
    private short[][] board;
    
    public Message(messageType mType, int id) {
    	this.type = mType;
    	this.id = id;
    }
    public Message(messageType mType, messageType mType2, int id) {
    	this.type = mType;
    	this.type2 = mType2;
    	this.id = id;
    }
    public void setSender(String user) {
    	this.sender = user;
    }
    public void setReceiver(String user) {
    	this.receiver = user;
    }
    public void setMessage(String messageContent) {
    	this.message = messageContent;
    }
    public void setId(int idNum) {
    	this.id = idNum;
    }
    public void setUsernames(ArrayList<String> userArr) {
    	this.usernames = userArr;
    }
    public void setBoard(short[][] currentBoard) {
    	this.board = currentBoard;
    }
    public messageType getType() {
    	return this.type;
    }
    public messageType getType2() {
    	return this.type2;
    }
    public String getSender() {
    	return this.sender;
    }
    public String getReceiver() {
    	return this.receiver;
    }
    public String getMessage() {
    	return this.message;
    }
    public int getId() {
    	return this.id;
    }
    public ArrayList<String> getUsernames() {
    	return this.usernames;
    }
    public short[][] getBoard() {
    	return this.board;
    }
}
