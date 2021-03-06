package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AMLReader extends BufferedReader {
	private int lineCount;
	AMLReader(FileReader f) {
		super(f);
		lineCount = 0;
	}
	
	int getLineCount() {
		return lineCount;
	}

	@Override
	public String readLine() throws IOException {
		String result = "";
		while (result.equals("")) {
			result = super.readLine();
			lineCount++;
			if(result==null) break;
			if (result.contains("//")) {
				result = result.substring(0, result.indexOf("//"));
			}
			result = result.trim();
		}
		return result;
	}
	@Override
	public void close() throws IOException {
		super.close();
	}
}
