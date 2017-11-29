import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

public class ArffReader {
	private static ArffReader reader = null;
	
	public static ArffReader getInstance() {
		if (reader == null)
			reader = new ArffReader();
		return reader;
	}
	
	/**
	 * @param filePath
	 * @return a BufferedReader for the given filePath, if it's invalid returns null
	 */
	public BufferedReader getBufferedReaderFromArffFile(String filePath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}
	
	/**
	 * @param filePath
	 * @return
	 */
	public Instances getInstances(String filePath) {
		Instances data = null;
		try {
			data = new Instances(reader.getBufferedReaderFromArffFile(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}	
