package application;

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
			CommandProcessor commandProcessor = new CommandProcessor(clientSocket.getOutputStream(), clientSocket.getInputStream());
			commandProcessor.process();
		} 
		catch(Exception e){
			System.out.println(e.getMessage());
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
