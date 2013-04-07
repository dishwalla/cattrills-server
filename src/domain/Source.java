package domain;

import java.util.List;

import application.SyncObj;

public class Source {

	protected List<Remember> gameData;
	protected int iterations;
	protected SyncObj so = new SyncObj();
	protected String gameResultAsString;

	public SyncObj getSo() {
		return so;
	}

	public void setSo(SyncObj so) {
		this.so = so;
	}
	public int getIterations(){
		return iterations;
	} 
	
	public void setIterations(int i){
		this.iterations = i;
	}

	public List<Remember> getGameData(){
		return gameData;
	}

	public void setGameData(List<Remember> r){
		this.gameData = r;
	}

	public String getGameResultAsString() {
		return gameResultAsString;
	}

	public void setGameResultAsString(String s) {
		this.gameResultAsString = s;
	}
	
}
