package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AMLReader extends BufferedReader {
	public AMLReader(FileReader f) {
		super(f);
	}

	public String readLine() throws IOException {
		String result = "";
		while (result.equals("")) {
			result = super.readLine();
			if(result==null) break;
			if (result.contains("//")) {
				result = result.substring(0, result.indexOf("//"));
			}
			result = result.trim();
		}
		return result;
	}

	public void close() throws IOException {
		super.close();
	}
}
