import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;


public class ChatServer {
	private static final int SERVER_PORT = 4447;
	
	public static final String serverMail = "java.project2021@gmail.com";
	
	public static int clientId = 0;

	public static List<Handler> activeClients = new ArrayList<Handler>();
	public static List<String> activeClientsNames = new ArrayList<String>();
	public static DefaultListModel<String> defListModel = new DefaultListModel<String>();
	
	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

			System.out.println("Server started and listening for connect requests");
			DBManager.connect();
			DBManager.createDB();
			
			while (true) {

				Socket clientSocket = serverSocket.accept();

				System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				Handler clientHandler = new Handler(clientSocket, "client" + clientId, in, out, clientId);

				Thread thread = new Thread(clientHandler);

				thread.start();

				clientId++;
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public static List<Handler> getActiveClients() {
		return activeClients;
	}

	public static void setActiveClients(List<Handler> activeClients) {
		ChatServer.activeClients = activeClients;
	}

	public static List<String> getActiveClientsNames() {
		return activeClientsNames;
	}

	public static void setActiveClientsNames(List<String> activeClientsNames) {
		ChatServer.activeClientsNames = activeClientsNames;
	}
}
