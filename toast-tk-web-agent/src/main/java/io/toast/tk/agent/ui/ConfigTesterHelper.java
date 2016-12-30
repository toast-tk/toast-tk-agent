package io.toast.tk.agent.ui;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.agent.web.RestRecorderService;

/**
 * Configuration panel
 */
public class ConfigTesterHelper {

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);
	private static final String FILE_DIRECTORY_SEPARATOR = "/";
	private static int timeout = 1000; // in milliseconds
	
	public static boolean testWebAppDirectory(String directory, boolean runTryValue, boolean fileOrDirectory)
			throws IOException {
		String fileName = directory.split(FILE_DIRECTORY_SEPARATOR)[directory.split(FILE_DIRECTORY_SEPARATOR).length - 1];
		if (directory.contains(" ")) {
			if (runTryValue) {
				NotificationManager.showMessage(directory + " has spaces in its name.").showNotification();
			}
			LOG.info("Status of " + directory + " : KO");
			return false;
		}

		if (fileOrDirectory) {
			if (testFileDirectory(directory, runTryValue, fileName)) {
				LOG.info("Status of " + directory + " : OK");
				return true;
			} else {
				LOG.info("Status of " + directory + " : KO");
				return false;
			}
		} else {
			if (testDirectory(directory, runTryValue, fileName)) {
				LOG.info("Status of " + directory + " : OK");
				return true;
			} else {
				LOG.info("Status of " + directory + " : KO");
				return false;
			}
		}
	}

	public static boolean testDirectory(String directory, boolean runTryValue, String fileName) {
		File myFile = new File(directory);
		if (myFile.exists()) {
			if (myFile.isDirectory()) {
				return true;
			} else {
				if (runTryValue) {
					NotificationManager.showMessage("You did not select a directory.").showNotification();
				}
				return false;
			}
		} else {
			if (runTryValue) {
				NotificationManager.showMessage("The directory : " + directory.split(fileName)[0] + " does not exist !")
						.showNotification();
			}
			return false;
		}
	}

	public static boolean testFileDirectory(String directory, boolean runTryValue, String fileName) {
		File myFile = new File(directory);
		if (myFile.exists()) {
			if (myFile.isFile()) {
				return true;
			} else {
				if (runTryValue) {
					NotificationManager.showMessage("You did not select a file.").showNotification();
				}
				return false;
			}
		} else {
			if (runTryValue) {
				NotificationManager
						.showMessage(fileName + " does not exist in the directory : " + directory.split(fileName)[0])
						.showNotification();
			}
			return false;
		}
	}

	public static boolean testWebAppUrl(String Url, boolean runTryValue) throws IOException {
		return testWebAppUrl(Url, runTryValue, null, null, null, null);					
	}

	public static boolean testWebAppUrl(String Url, boolean runTryValue, String proxyAdress, String proxyPort,
			String proxyUserName, String proxyUserPswd) throws IOException {
		if (Url.contains(" ")) {
			if (runTryValue) {
				NotificationManager.showMessage(Url + " has spaces in its name.").showNotification();
			}
			LOG.info("Status of " + Url + " : KO");
			return false;
		}

		if (getStatus(Url,proxyAdress, proxyPort, proxyUserName, proxyUserPswd)) {
			LOG.info("Status of " + Url + " : OK");
			return true;
		} else {
			if (runTryValue) {
				NotificationManager.showMessage(Url + " does not answer.").showNotification();
			}
			LOG.info("Status of " + Url + " : KO");
			return false;
		}
	}
	
	public static boolean getStatus(String url, String proxyAdress, String proxyPort, String proxyUserName,
			String proxyUserPswd) throws IOException {
		boolean result = false;
		try {
			URL siteUrl = new URL(url);
			HttpURLConnection connection = null;
			if (proxyUserName != null && proxyUserPswd != null) {
				Authenticator authenticator = new Authenticator() {

					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(proxyUserName, proxyUserPswd.toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
			}

			if (proxyAdress != null && proxyUserName != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(proxyAdress, Integer.parseInt(proxyPort)));
				connection = (HttpURLConnection) siteUrl.openConnection(proxy);
			} else
				connection = (HttpURLConnection) siteUrl.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(timeout);
			connection.connect();

			int code = connection.getResponseCode();
			if (code == 200) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
}