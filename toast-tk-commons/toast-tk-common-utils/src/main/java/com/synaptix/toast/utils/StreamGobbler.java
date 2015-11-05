package com.synaptix.toast.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

	InputStream is;

	String type;

	private File outputFile;

	public StreamGobbler(
		InputStream is,
		String type,
		String outputFile) {
		this.is = is;
		this.type = type;
		this.setName("StreamGobbler - " + type);
		this.setPriority(MAX_PRIORITY);
		this.setDaemon(true);
		this.outputFile = new File(outputFile);
		if(this.outputFile.exists()) {
			this.outputFile.mkdirs();
		}
	}

	public void run() {
		FileWriter w = null;
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			w = new FileWriter(this.outputFile);
			while((line = br.readLine()) != null)
				w.append(line + "\n");
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if(w != null) {
				try {
					w.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}