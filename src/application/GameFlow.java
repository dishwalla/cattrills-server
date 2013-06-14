package application;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import domain.Remember;
import domain.Source;

public class GameFlow {

	protected PrintWriter writer;
	protected BufferedReader breader;
	boolean valueSet = false;
	protected String partyName;

	public GameFlow(PrintWriter wr, BufferedReader br){
		this.writer = wr;
		this.breader = br;
	}

	public void startGame() throws Exception {
		writer.println("How many questions will be there in the game?");
		int iterations = 0;
		boolean isValidInput = false;
		while (!isValidInput){
			String s = breader.readLine();
			try{
				iterations = Integer.parseInt(s);
				isValidInput = true;
			} catch(Exception e){
				writer.println("Please enter valid number");
			}
		}
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Map<String, String> gamePares = MultiServer.gamePares;
		Source source = thisThread.getSource();
		source.setIterations(iterations);
		SyncObj so = source.getSo();
		askAndNotify();
		for(int i=1; i<iterations; i++){
			so.doWait();
			answerAndAsk(writer, breader);
		}
		so.doWait();
	//	writer.println("\n");
		
		CommandProcessor.showEvr(writer, breader, Utils.obtainMyName(), gamePares.get(Utils.obtainMyName()));
		so.doNotify();
		CommandProcessor.goOn(writer, breader);
	//	CommandProcessor.saveResults(writer, breader);
		CommandProcessor.cleanUp();
	}

	public void askAndNotify() throws Exception {
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Source source = thisThread.getSource();
		SyncObj so = source.getSo();
		List<Remember> list = source.getGameData();
		writer.println("Write your question: ");
		String quest = breader.readLine();
		Remember r = new Remember();
		r.setQuestion(quest);
		list.add(r);
		so.doNotify();

	}

	public static void answerAndAsk(PrintWriter writer, BufferedReader breader) throws Exception{
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Source source = thisThread.getSource(); 
		SyncObj so = source.getSo();
		List<Remember> list = source.getGameData();
		if(list.size() == 0) {so.doWait();}
		Remember r = list.get(list.size()-1);
		String s = r.getQuestion();
		writer.println("The question is: " + CommandProcessor.firstWord(s).toUpperCase() + ", write your answer");
		String ans = breader.readLine();
		r.setAnswer(ans);
		Remember r1 = new Remember();
		writer.println("Write your question: ");
		String quest2 = breader.readLine();
		r1.setQuestion(quest2);
		list.add(r1);
		so.doNotify();
		
	}
	
	
}