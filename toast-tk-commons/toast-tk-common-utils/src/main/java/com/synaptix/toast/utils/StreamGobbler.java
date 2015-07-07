/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 9 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */
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