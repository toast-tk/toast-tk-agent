package com.synaptix.toast.automation.repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Download {

	private static final Logger LOG = LogManager.getLogger(Download.class);
	
	public static String getFile(String host, String destination) {
		InputStream input = null;
		FileOutputStream writeFile = null;
		String fileName = null;
		try {
			URL url = new URL(host);
			URLConnection connection = url.openConnection();
			int fileLength = connection.getContentLength();
			if (fileLength == -1) {
				LOG.error("Invalide URL or file: " + host);
				return null;
			}

			input = connection.getInputStream();
			fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);

			writeFile = new FileOutputStream(destination != null ? destination + "/" + fileName : fileName);
			byte[] buffer = new byte[1024];
			int read;

			while ((read = input.read(buffer)) > 0)
				writeFile.write(buffer, 0, read);
			writeFile.flush();
			LOG.info("Downloaded: {} from {}", fileName, url);
			return destination + "/" + fileName;
		} catch (IOException e) {
			LOG.error("Error while trying to download file : " + destination + "/" + fileName);
			e.printStackTrace();
		} finally {
			try {
				writeFile.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		getFile("http://10.106.184.115:8080/toast/agent-lib/", "D:/tmp");
	}
}