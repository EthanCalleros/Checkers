import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    
    public enum messageType {
    	id,
    	username,
    	password,
    	username_taken,
    	username_free,
    	bad_password,
    	ok_password,
    	correct_password,
    	incorrect_password,
    	user_exists,
    	user_dne,
    	login,
    	signup,
    	group_message,
    	direct_message,
    	receive_message,
    	user_joined,
    	user_left,
    	board,
    	game_move,
    	game_start,
    	game_win,
    	game_lose,
    	forfeit,
    	new_users
    }
    
    private int id;
    private messageType type;
    private messageType type2;
    private String sender;
    private String receiver;
    private String message, message2;
    private ArrayList<String> usernames;
    private boolean isRed;
    
    private int fromRow, fromCol, toRow, toCol;
    
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
    public void setMessage2(String messageContent) {
    	this.message2 = messageContent;
    }
    public void setId(int idNum) {
    	this.id = idNum;
    }
    public void setUsernames(ArrayList<String> userArr) {
    	this.usernames = userArr;
    }
    public void setMove(int fromRow, int fromCol, int toRow, int toCol) {
    	this.fromRow = fromRow;
    	this.fromCol = fromCol;
    	this.toRow = toRow;
    	this.toCol = toCol;
    }
    public void setIsRed(boolean isRed) {
    	this.isRed = isRed;
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
    public String getMessage2() {
    	return this.message2;
    }
    public int getId() {
    	return this.id;
    }
    public ArrayList<String> getUsernames() {
    	return this.usernames;
    }
    public int getFromRow() {
    	return this.fromRow;
    }
    public int getFromCol() {
    	return this.fromCol;
    }
    public int getToRow() {
    	return this.toRow;
    }
    public int getToCol() {
    	return this.toCol;
    }
    public boolean getIsRed() {
    	return this.isRed;
    }
}
