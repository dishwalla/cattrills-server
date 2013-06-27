package application;

import domain.Source;

public class Utils {

	public static String obtainMyName(){
		return ((CommandProcessorThread)Thread.currentThread()).getMyName();
	}

	public static Source getMySource(){
		CommandProcessorThread thread = (CommandProcessorThread)Thread.currentThread();
		return thread.getSource();
	}
	public static String localMach()throws Exception{
		return ((CommandProcessorThread)Thread.currentThread()).getMachineName();

	}
}
