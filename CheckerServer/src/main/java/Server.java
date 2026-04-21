import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ClientThread waitingPlayer = null;
	TheServer server;
	private Consumer<Serializable> callback;
	
	HashMap<String, String> accounts; // Wanted to make this a file or database to store passwords long term
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		accounts = new HashMap<String,String>();
		server.start();
	}
	
	private void sendUserList() {
        ArrayList<String> usernames = new ArrayList<>();
        for (ClientThread cl:clients) {
            if (cl.username != null) usernames.add(cl.username);
        }
        for (ClientThread cl:clients) {
            try {
                Message msg = new Message(Message.messageType.new_users, 0);
                msg.setUsernames(usernames);
                cl.out.writeObject(msg);
                cl.out.reset();;
            } catch (Exception e) {}
        }
    }
	
	
	public class TheServer extends Thread{
		
		public void run() {
            try (ServerSocket mysocket = new ServerSocket(5555)) {
                System.out.println("Server is waiting for a client!");
                while (true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: client #" + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            } catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }
	}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			String username;
			ObjectInputStream in;
			ObjectOutputStream out;
			ClientThread opp;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
					
					Message idMsg = new Message(Message.messageType.id, count);
					out.writeObject(idMsg);
					out.reset();
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
					
				 while(true) {
					    try {
					    	Message msg = (Message)in.readObject();

					    	if (msg.getType() == Message.messageType.login) {
					    		String user = msg.getMessage();
					    		String password = msg.getMessage2();
					    		
					    		
					    		if (accounts.containsKey(user) && accounts.get(user).equals(password) == false){
					    			Message badPassword = new Message(Message.messageType.incorrect_password, count);
					    			out.writeObject(badPassword);
					    			out.reset();
					    			callback.accept("client #" + count + " Bad Password");
					    		} else if (accounts.containsKey(user) == false) {
					    			Message userDne = new Message(Message.messageType.user_dne, count);
					    			out.writeObject(userDne);
					    			out.reset();
					    			callback.accept("client #" + count + " User Does Not Exist");
					    		} else if (accounts.containsKey(user) && accounts.get(user).equals(password)) {
					    		    username = user;
					    		    Message success = new Message(Message.messageType.correct_password, Message.messageType.user_exists, count);
					    		    out.writeObject(success);
					    		    out.reset();
					    		    callback.accept("client #" + count + " logged in as " + username);
					    		    sendUserList();
					    		}
					    	} else if (msg.getType() == Message.messageType.signup) {
					    		String user = msg.getMessage();
					    		String password = msg.getMessage2();
					    		
					    		String specialChars = "[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?]";
					    		String nums = "[0-9]";
					    		
					    		if (!password.matches(".*" + specialChars + ".*") || !password.matches(".*" + nums + ".*")){
					    			Message badPassword = new Message(Message.messageType.bad_password, count);
					    			out.writeObject(badPassword);
					    			out.reset();
					    			callback.accept("client # " + count + " Bad Password: " + msg.getMessage());
					    		} else if (accounts.containsKey(user) == false) {
					    			accounts.put(user, password);
					    			username = user;
					    			Message login = new Message(Message.messageType.username_free, Message.messageType.ok_password, count);
					    			out.writeObject(login);
					    			out.reset();
					    			callback.accept("client #" + count + " as: " + username + " password: " + password);
					    			sendUserList();
					    		} else if (accounts.containsKey(user)) {
					    			Message userExists = new Message(Message.messageType.user_exists, count);
					    			out.writeObject(userExists);
					    			out.reset();
					    			callback.accept("client # " + count + " User Already Exists: " + user);
					    		}
					    	} else if (msg.getType() == Message.messageType.game_start) {
					    		if (waitingPlayer == null) {
					    			waitingPlayer = this;
					    			callback.accept(username + " is waiting for an opponent...");
					    		} else {
					    			ClientThread oppThread = waitingPlayer;
					    			waitingPlayer = null;
					    			
					    			this.opp = oppThread;
					    			oppThread.opp = this;
					    			
					    			try {
					    				Message thisMsg = new Message(Message.messageType.board, Message.messageType.game_start, count);
					    				Message oppMsg = new Message(Message.messageType.board, Message.messageType.game_start, oppThread.count);
					    				
					    				thisMsg.setIsRed(true);
					    				oppMsg.setIsRed(false);
					    				
					                    this.out.writeObject(thisMsg); 
					                    this.out.reset();

					                    oppThread.out.writeObject(oppMsg);
					                    oppThread.out.reset();

					                    callback.accept("Matched: " + this.username + " vs " + oppThread.username);
					                } catch (Exception e) {
					                    callback.accept("Error starting game between " + username + " and opponent");
					                }
					    		}
					    	} else if (msg.getType() == Message.messageType.game_move) {
					    		if (this.opp != null) {
					    	        try {
					    	        	Message moveMsg = new Message(Message.messageType.board, Message.messageType.game_move, count);
					    	        	moveMsg.setMove(msg.getFromRow(), msg.getFromCol(), msg.getToRow(), msg.getToCol());
					    	            this.opp.out.writeObject(moveMsg);
					    	            this.opp.out.reset();
					    	            
					    	            callback.accept("Move relayed: " + username + " -> " + opp.username + 
					    	                            " [" + msg.getFromRow() + "," + msg.getFromCol() + "] to " +
					    	                            "[" + msg.getToRow() + "," + msg.getToCol() + "]");
					    	        } catch (Exception e) {
					    	            callback.accept("Failed to relay move from " + username + ". Opponent may have disconnected.");
					    	        }
					    	    } else {
					    	        callback.accept("Error: " + username + " tried to move but has no opponent!");
					    	    }
					    	} else if (msg.getType() == Message.messageType.game_win) {
					    		Message winMsg = new Message(Message.messageType.board, Message.messageType.game_win, count);
					    		out.writeObject(winMsg);
					    	} else if (msg.getType() == Message.messageType.game_lose) {
					    		Message loseMsg = new Message(Message.messageType.board, Message.messageType.game_lose, count);
					    		out.writeObject(loseMsg);
					    	} else if (msg.getType() == Message.messageType.forfeit) {
					    		for (ClientThread cl : clients) {
					    	        if (!cl.username.equals(username)) {
					    	            try {
					    	                Message winMsg = new Message(Message.messageType.board, Message.messageType.forfeit, count);
					    	                winMsg.setMessage(username + " forfeited. You win!");
					    	                cl.out.writeObject(winMsg);
					    	                cl.out.reset();
					    	            } catch (Exception e) {}
					    	        }
					    	    }
					    	} else if (msg.getType() == Message.messageType.group_message) {
					    		callback.accept(username + " to All: " + msg.getMessage());
		                        for (ClientThread cl : clients) {
		                            try {
		                                Message send = new Message(Message.messageType.group_message, count);
		                                send.setMessage(username + " to All: " + msg.getMessage());
		                                cl.out.writeObject(send);
		                                cl.out.reset();
		                            } catch (Exception e) {}
		                        }
					    	} else if (msg.getType() == Message.messageType.direct_message) {
					    		callback.accept(username + " to " + msg.getReceiver() + ": " + msg.getMessage());
		                        for (ClientThread cl : clients) {
		                            if (cl.username != null && cl.username.equals(msg.getReceiver())) {
		                                try {
		                                    Message send = new Message(Message.messageType.direct_message, count);
		                                    send.setMessage(username + " (direct): " + msg.getMessage());
		                                    cl.out.writeObject(send);
		                                    cl.out.reset();
		                                } catch (Exception e) {}
		                            }
		                        }
					    	}
					    	
					    	
					    	}
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	clients.remove(this);
					    	sendUserList();
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	