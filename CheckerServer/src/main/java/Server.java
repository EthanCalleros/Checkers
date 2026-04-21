import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
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
					    	boolean taken = false;
					    	if (msg.getType() == Message.messageType.username) {
					    		
					    		for (ClientThread cl:clients) {
					    			if (cl != this && msg.getMessage().equals(cl.username)) {
					    				taken = true;
					    				break;
					    			}
					    		}
					    		if (taken) {
						    		Message takenMsg = new Message(Message.messageType.username_taken, count);
						    		out.writeObject(takenMsg);
						    		out.reset();
						    		callback.accept("client #" + count + " taken usernam: " + msg.getMessage());
						    	} else {
						    		username = msg.getMessage();
						    		Message freeMsg = new Message(Message.messageType.username_free, count);
						    		out.writeObject(freeMsg);
						    		out.reset();
						    		callback.accept("client #" + count + " as: " + username);
						    		sendUserList();
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


	
	

	
