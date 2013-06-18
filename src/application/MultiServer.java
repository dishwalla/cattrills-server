package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MultiServer {

	public static Map<String, Thread> clients = new HashMap <String, Thread>(); 
	public static Map<String, String> gamePares = new HashMap<String, String>();
	public static List<String> selectedClients = new LinkedList<String>();
	

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null; 
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(1234);
		}
		catch(IOException e){
			System.err.println("Could not listen on port 1234");
			System.exit(-1);
		}

		while (listening){
			
			Socket clientSocket = serverSocket.accept();
			//Socket client = serverSocket.close();
			//String ad = clientSocket.getInetAddress().getHostAddress();
			InetAddress add = clientSocket.getInetAddress();
			String host = add.getHostName();
					
			/*InetAddress addr = clientSocket.getLocalAddress();
			String hostname = addr.getHostName(); // server'gameResultAsString name*/
			System.out.println(host);
				
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println("Write your name: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String name = in.readLine().toUpperCase();
			while (clients.containsKey(name) || name.contains(" ")){
				out.println("This name already exists, or contains space choose another:");
				name = in.readLine().toUpperCase();
			}
			out.println("Your name've been chosen");
			CommandProcessorThread clientThread = new CommandProcessorThread(clientSocket);
			clientThread.setMyName(name);
			clientThread.setMachineName(host);
			clients.put(name, clientThread);
			
			clientThread.start();
			System.out.println("New client " + name + " connected!");

		}

	}


}




