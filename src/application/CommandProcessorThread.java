package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import domain.Source;

public class CommandProcessorThread extends Thread {

	protected Socket clientSocket;
	protected String myName;
	protected String machineName;
	protected Source source; 
	
	public CommandProcessorThread(Socket s){
		clientSocket = s;
	}

	@Override
	public void run() {
		System.out.println("CommandProcessor started");
		try {
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
			out.println("Write your name: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

			String name = in.readLine().toUpperCase();
			while (MultiServer.clients.containsKey(name) || name.contains(" ")){
				out.println("This name already exists, or contains space choose another:");
				name = in.readLine().toUpperCase();
			}
			out.println("Your name've been chosen");
			setMyName(name);
			MultiServer.clients.put(name, this);
			System.out.println("New client " + name + " connected!"); 
			CommandProcessor commandProcessor = new CommandProcessor(clientSocket.getOutputStream(), clientSocket.getInputStream());
			commandProcessor.process();
			Thread i = currentThread();
		//	Thread i = MultiServer.clients.get(name);
			check(i, clientSocket, name);
		}
		catch(ClientConnectionResetException e){
			CommandProcessor.closeConnection();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	public static void check(Thread cpt, Socket s, String n) throws IOException{
		boolean flag = true;
		while (flag) {		
			try {
				boolean t = cpt.isAlive();
				if (t){
				/*	MultiServer.clients.remove(n);
					System.out.println("CONNECTION TERMINATED!");
					cpt.currentThread().interrupt();
					s.close();		*/
					CommandProcessor.closeConnection();
					flag = false;
				}
				else return;
			}
			catch(Exception e){
				System.err.println(e);
			}
		}
	}
	public String getMyName(){
		return myName;
	}

	public void setMyName(String s){
		this.myName = s;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
}
