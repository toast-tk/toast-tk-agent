package io.toast.tk.agent.ui.utils;

import io.toast.tk.agent.ui.NotificationManager;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Configuration panel
 */
public class ConfigTesterHelper {

	private static final String FILE_DIRECTORY_SEPARATOR = "/";
	private static final int timeout = 500;
	
	public static boolean testWebAppDirectory(String directory, boolean runTryValue, boolean fileOrDirectory)
			throws IOException {
		String fileName = directory.split(FILE_DIRECTORY_SEPARATOR)[directory.split(FILE_DIRECTORY_SEPARATOR).length - 1];
		
		boolean isKo = true;
		if (fileOrDirectory) {
			if (testFileDirectory(directory, runTryValue, fileName)) {
				isKo = false;
			}
		} else {
			if (testDirectory(directory, runTryValue, fileName)) {
				isKo = false;
			}
		}
		return !isKo;
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

	public static boolean testWebAppUrl(String url, boolean runTryValue) throws IOException {
		return testWebAppUrl(url, runTryValue, null, null, null, null);					
	}

	public static boolean testWebAppUrl(String url, boolean runTryValue, String proxyAdress, String proxyPort,
			String proxyUserName, String proxyUserPswd) throws IOException {

		boolean isKo = true;
		if (url.contains(" ")) {
			if (runTryValue) {
				NotificationManager.showMessage(url + " has spaces in its name.").showNotification();
			}
		}

		if (getStatus(url,proxyAdress, proxyPort, proxyUserName, proxyUserPswd)) {
			isKo = false;
		} else {
			if (runTryValue) {
				NotificationManager.showMessage(url + " does not answer.").showNotification();
			}
		}
		return !isKo;
	}
	
	
	
	public static boolean getStatus(String url, String proxyAdress, String proxyPort, String proxyUserName,
			String proxyUserPswd) throws IOException {

	        boolean result = false;
	        Proxy proxy = null;
	        if(proxyAdress != null && proxyPort != null) {
				InetSocketAddress proxyInet = new InetSocketAddress(proxyAdress,Integer.parseInt(proxyPort));
				proxy = new Proxy(Proxy.Type.HTTP, proxyInet);
	        }
	        
	        Authenticator.setDefault (new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication (proxyUserName, proxyUserPswd.toCharArray());
	            }
	        });

			try {
				HttpURLConnection connection;
				if(url.startsWith("https")){
					connection = pingHttpsUrl(url, proxy);
				} else {
					connection = pingHttpUrl(url, proxy);
				}
				int code = connection.getResponseCode();
				if(code == 301){
					result = getStatus(connection.getHeaderField("Location"), proxyAdress, proxyPort, proxyUserName, proxyUserPswd);
				} else if (code == 200) {
					result = true;
				}
			} catch(SSLHandshakeException sslException){
	            try {
					HttpsURLConnection connection = pingHttpsUrl(url, proxy);
					int code = connection.getResponseCode();
					if(code == 301){
						result = getStatus(connection.getHeaderField("Location"), proxyAdress, proxyPort, proxyUserName, proxyUserPswd);
					} else if (code == 200) {
						result = true;
					}
	            } catch (Exception e) {
	                result = false;
	            }
	        } catch (SocketTimeoutException e) {
				return false;
			}
			catch (Exception e) {
	            result = false;
	        }
	        return result;
	}
	
	public static HttpURLConnection pingHttpUrl(String url, Proxy proxy) throws IOException {
		URL siteURL = new URL(url);
		HttpURLConnection connection;
		if(url.contains("localhost") || proxy == null){
			connection = (HttpURLConnection) siteURL.openConnection();
		} else {
			connection = (HttpURLConnection) siteURL.openConnection(proxy);		
		}
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(timeout);
		connection.connect();
		return connection;
	}

	public static HttpsURLConnection pingHttpsUrl(String url, Proxy proxy) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		URL siteURL = new URL(url);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
			
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		HttpsURLConnection connection = null;
		
		if(proxy != null) {
			connection = (HttpsURLConnection) siteURL.openConnection(proxy);
		} else {
			connection = (HttpsURLConnection) siteURL.openConnection();
		}
		
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(timeout);
		connection.connect();

		return connection;
	}
}