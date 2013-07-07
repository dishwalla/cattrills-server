package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class BRWithNullHandling extends BufferedReader {

	public BRWithNullHandling(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {

		String str = super.readLine();
		if (str != null) {
			return str;
		}
		else throw new ClientConnectionResetException("Client seems to be disconnected");
	}


}
