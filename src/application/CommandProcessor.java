package application;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import domain.Remember;
import domain.Source;

public class CommandProcessor {

	protected OutputStream out;
	protected InputStream in;
	boolean valueSet = false;
	protected SyncObj so;
	protected String partyName;

	public CommandProcessor(OutputStream out, InputStream in) {
		super();
		this.out = out;
		this.in = in;

	}

	public void process() throws Exception {
		BufferedReader reader = new BRWithNullHandling(new InputStreamReader(in, "UTF-8")); 
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"), true);
		String inputLine; 
		inputLine = reader.readLine();
		while (inputLine != null){
			if(inputLine.equals("list")){
				System.out.println(inputLine);
				list(writer);
			}
			else if (inputLine.equals("select")){
				System.out.println(inputLine);
				select(writer, reader);

			}
			else {
				System.out.println(inputLine);
				writer.println("Unrecognized command");
			}

			if (MultiServer.selectedClients.contains(Utils.obtainMyName())){
				CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
				SyncObj so = thisThread.getSource().getSo();
				//	List<String> s = MultiServer.selectedClients;
				Map<String, String> gamePares = MultiServer.gamePares;
				String inviter = gamePares.get(Utils.obtainMyName().toString());
				//Utils.getMySource().getSo().doWait();
				writer.println("You've been selected for game, would you participate? " + inviter);
				String YN = reader.readLine();
				if(YN.equals("n") || YN.equals("N")){
					MultiServer.selectedClients.remove(Utils.obtainMyName());
					so.doNotify();
				}
				else{
					so.doNotify();
					so.doWait();
					writer.println("You've accept the game, there will be " + Utils.getMySource().getIterations() + " questions");					
					gamePares = MultiServer.gamePares;
					partyName = gamePares.get(Utils.obtainMyName());

					for(int i=0; i<Utils.getMySource().getIterations()-1; i++){

						GameFlow.answerAndAsk(writer, reader);
						so.doWait();
					}
					answer(writer, reader);
					so.doNotify();
					showEvr(writer, reader, partyName, Utils.obtainMyName());
					//	saveResults(writer, reader);
					//if (!goOn(writer, reader)){
					goOn(writer, reader);
					//	return;
					//	}
					//	so.doWait();
					cleanUp();
				}
			}
			writer.println("Write command:");
			inputLine = reader.readLine();
		}

	}

	public static String firstWord (String snt) throws Exception{

		String strM = snt.concat(" ");
		String str="";
		String str2 = "";
		String strFin = "";	
		char[] chars = strM.toCharArray();
		int t = 0;
		for (int i=0; i<strM.length(); i++){
			if (chars[i] == ' '){
				t = t+1;
				strFin = str;
				if(t ==2 ) {
					break;
				}
			}
			str += chars[i];
		}
		char[] chars2 = strFin.toCharArray();
		for (int j=0; j<snt.length(); j++){
			if (chars2[j] == ' '){
				break;
			}
			str2 += chars[j];
		}
		String[] CompQ = {"v", "vo", "k", "o", "c", "y", "u", "po", "ot", "dlya", "na", "s", "в", "во", "к", "о", "с", "у", "по", "от", "для", "на"};
		List<String> wordList = Arrays.asList(CompQ);
		String newStr = str2.toString();
		if ( wordList.contains(newStr)){
			return strFin;
		}
		else return str2;

	}

	public void list(PrintWriter pw){

		Map<String, Thread> clients = MultiServer.clients;
		//int i = 0;
		for(String key : clients.keySet()){
			pw.write(key + "\t");
			//	i++;
		}
		/*int i = 1;
		for(String key : clients.keySet()){
			pw.write(i + "." + key + "\t");
			i++;
		}*/
		pw.println("");
	}

	public void select(PrintWriter pw, BufferedReader br) throws Exception {
		//	pw.println("Write the name of you'r party:");
		String name = br.readLine().toUpperCase();

		Map<String, Thread> clients = MultiServer.clients;
		Map<String, String> gamePares = MultiServer.gamePares;
		if (clients.containsKey(name)){
			if(name.equals(Utils.obtainMyName())){
				pw.println("It'yours name, choose another gamer!");	
				return;
			}
			List<String> sc = MultiServer.selectedClients;
			if (sc.contains(name)){
				pw.println("Client is busy now!");
				return;
			}
			partyName = name;
			gamePares.put(Utils.obtainMyName(), name);
			gamePares.put(name, Utils.obtainMyName());

			List<Remember> rm = new LinkedList<Remember>();
			Source s = new Source();
			s.setGameData(rm);

			CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();

			thisThread.setSource(s);
			CommandProcessorThread anotherThread = (CommandProcessorThread)clients.get(name); 
			anotherThread.setSource(s);
			sc.add(name);
			sc.add(Utils.obtainMyName()); //NEW!!!
			s.getSo().doWait();
			if(!sc.contains(name)){
				pw.println("Client rejected the game, chose another partner!");
				MultiServer.selectedClients.remove(Utils.obtainMyName());
				return;
			}


			pw.println("Client have been choosen");

			GameFlow gf = new GameFlow(pw, br);
			gf.startGame();
		}

		else { 
			pw.println("No such client!");
		}

	}

	public void answer (PrintWriter pw, BufferedReader br) throws Exception {
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Source source = thisThread.getSource(); 
		List<Remember> list = source.getGameData();
		Remember r = list.get(list.size()-1);
		String s = r.getQuestion();
		pw.println("The question is: "  + CommandProcessor.firstWord(s).toUpperCase() + "?" + ", write your answer");
		String ans = br.readLine();
		r.setAnswer(ans);
		pw.println("\n");
	}

	public static void showEvr(PrintWriter pw, BufferedReader br, String askN, String ansN) throws Exception{
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Source source = thisThread.getSource();

		List<Remember> list = source.getGameData();
		String s = "";
		String temp;
		//ansN = obtainMyName();
		//askN = partyName;

		for(Remember r : list){
			s += askN + " : " + r.getQuestion() + "?" + "\n";
			s += ansN + " : " + r.getAnswer() + "." + "\n";
			temp = askN;
			askN = ansN;
			ansN = temp;
		}
		pw.println(s+ "\n");
		source.setGameResultAsString(s);

	}

	public static void saveResults(PrintWriter pw, BufferedReader br) throws IOException{
		boolean flag = true;
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
		Source source = thisThread.getSource();

		while (flag){
			pw.println("Do you want to save results? y/n");
			String des;
			//String gameResultAsString = "";
			des = br.readLine();
			FileWriter writer = null;
			String path="";
			String fileName="";
			char r = des.charAt(0);
			if (r == 'y' || r == 'Y'){

				try
				{
					String s = source.getGameResultAsString();
					pw.println("Specify the path and name of your file: ");
					path = br.readLine();
					pw.println("Specify the name of your file: ");
					fileName = br.readLine();
					//String hostName = Utils.localMach();
					//String l = "localhost";
					//if (hostName.toString() == l)
					//path = ("\\\\" + hostName + "\\c$" + path + "\\" + fileName);
					path = ("/" + path + "/" + fileName);
					//else if (hostName != l) 
					//path = ("\\\\" + hostName + "\\c$" + path + "\\" + fileName);
					writer = new FileWriter(path,true); 
					byte buf[]= s.getBytes();

					for (int i = 0; i < buf.length; i++){
						writer.write(buf[i]);

					}
					writer.write("\n\n"); 
					flag = false;
				}catch(Exception e){
					System.out.print(e.getMessage()); }
				finally
				{
					try{
						writer.close();
						flag = false;
					}catch(IOException e)
					{System.out.print(e.getMessage()); }
				}
			}
			else if(r == 'n' || r == 'N'){
				flag = false;	
			}
		}
	}

	public static boolean goOn(PrintWriter pw, BufferedReader br) throws IOException{

		pw.println("Do you want to quit? y/n");
		String des;
		des = br.readLine();
		char r = des.charAt(0);
		if (r == 'y' || r == 'Y'){
			try
			{
				closeConnection();
				return false;
			}
			catch(Exception e){
				System.out.print(e.getMessage());
			} 
		}
		else if(r == 'n' || r == 'N'){
			return true;
		}
		return true;
	}

	public static void cleanUp() throws Exception{
		CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();

		Map<String, String> gamePares = MultiServer.gamePares;
		List<String> sc = MultiServer.selectedClients;
		Source source = thisThread.getSource();
		String name = Utils.obtainMyName();
		sc.remove(name);
		gamePares.remove(name);
		source.getGameData().clear();
	}

	public static void closeConnection(){
		try {
			CommandProcessorThread thisThread = (CommandProcessorThread)Thread.currentThread();
			Map<String, Thread> clients = MultiServer.clients;
			String name = Utils.obtainMyName();
			clients.remove(name);
			System.out.println("Client " + name + " disconnected!");
			thisThread.interrupt();
			thisThread.clientSocket.close();}
		catch(Exception e){
			System.err.println(e);
		}
	}


}
