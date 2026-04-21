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

public class Server {

	int count = 1;
	HashMap<String, ClientThread> clients = new HashMap<>();
	TheServer server;
	private Consumer<Serializable> callback;


	Server(Consumer<Serializable> call) {

		callback = call;
		server = new TheServer();
		server.start();
	}

	public void broadcastUserList() {
		Message listMsg = new Message();
		listMsg.type = 5;
		synchronized (clients) {
			ArrayList<String> recip = new ArrayList<>();
			recip.add("All");
			recip.addAll(clients.keySet());
			listMsg.recipients = recip;
		}
		for (ClientThread t : clients.values()) {
			try {
				t.out.writeObject(listMsg);
				t.out.reset();
			} catch (Exception e) {}
		}
	}



	public class TheServer extends Thread {

		public void run() {

			try (ServerSocket mysocket = new ServerSocket(5555);) {
				System.out.println("Server is waiting for a client!");


				while (true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					//callback.accept("client has connected to server: " + "client #" + count);
					//clients.add(c);
					c.start();

					count++;

				}
			}//end of try
			catch (Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}


	class ClientThread extends Thread {


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		String userName = "";

		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
		}

		public void sending(Message msg) {
			synchronized (clients) {
				for (ClientThread t : clients.values()) {
					try {
						t.out.writeObject(msg);
						t.out.reset();
					} catch (Exception e) {}
				}
			}
		}

		public void run() {

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);

				while (true) {
					Message data = (Message) in.readObject();

					if (data.type == 1) {
						if (clients.containsKey(data.sender)) {
							Message error = new Message();
							error.type = 4; // Error type
							error.messageText = "Username taken!";
							out.writeObject(error);
							out.reset();
						} else {
							this.userName = data.sender;
							synchronized (clients) {
								clients.put(userName, this);
							}
							callback.accept(data);
							out.writeObject(data);
							out.reset();
							broadcastUserList();
						}
					}

					else if (data.type == 3) {
						callback.accept(data);
						for (String recipient : data.recipients) {
							if (clients.containsKey(recipient)) {
								clients.get(recipient).out.writeObject(data);
								clients.get(recipient).out.reset();
							}
						}
					}

					else if (data.type == 2) {
						callback.accept(data);
						sending(data);
					}
				}
			} catch (Exception e) {
				callback.accept("Client " + (userName.isEmpty() ? count : userName) + " disconnected.");
				synchronized (clients) {
					clients.remove(this.userName);
				}
				broadcastUserList();
			}
		}

	}
}