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

Creation date: 2 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.swing.agent;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.event.message.LoadingMessage;
import com.synaptix.toast.swing.agent.event.message.StatusMessage;
import com.synaptix.toast.swing.agent.event.message.StopLoadingMessage;
import com.synaptix.toast.swing.agent.ui.ConfigPanel;
import com.synaptix.toast.utils.DownloadUtils;

public class StudioApplicationImpl implements IStudioApplication {

	private static final Logger LOG = LogManager.getLogger(StudioApplicationImpl.class);
	private final EventBus eventBus;
	private final ISwingAutomationClient serverClient;
	private final Config config;
	private final Properties properties;
	private final File toastPropertiesFile;


	@Inject
	public StudioApplicationImpl(final Config config, final EventBus eventBus, final ISwingAutomationClient serverClient) {
		this.eventBus = eventBus;
		this.serverClient = serverClient;
		this.toastPropertiesFile = new File(Property.TOAST_PROPERTIES_FILE);
		this.properties = new Properties();
		initWorkspace(this.config = config);
		
		if(!serverClient.isConnectedToWebApp()){
			String message = String.format("The webapp looks down @%s:%s, please check your configuration and restart the agent !", config.getWebAppAddr(), config.getWebAppPort());
			JOptionPane.showMessageDialog(null, message);
			System.exit(-1);
		}
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				LOG.info("t name = " + t.getName(), e);
				LOG.info(" : " + e.getStackTrace());

				if (e instanceof OutOfMemoryError) {
					LOG.error("Killing myself because of Out Of Memory error !");
					System.exit(-1);
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	private void initWorkspace(final Config config) {
		if (config.getUserHome() != null) {
			try {
				String workSpaceDir = config.getWorkSpaceDir();
				new File(workSpaceDir).mkdir();
				new File(config.getPluginDir()).mkdir();
				new File(workSpaceDir + "/log").mkdir();
				boolean isNewEnv = false;
				File toastProperties = new File(workSpaceDir + "/toast.properties");
				
				if (!toastProperties.exists()) {
					isNewEnv = true;
					toastProperties.createNewFile();
				}
				downloadPlugins(config);
				
				Properties p = new Properties();
				p.setProperty(Property.TOAST_RUNTIME_TYPE, config.getRuntimeType());
				p.setProperty(Property.TOAST_RUNTIME_CMD, config.getRuntimeCommand());
				p.setProperty(Property.TOAST_RUNTIME_AGENT, config.getPluginDir() + Property.AGENT_JAR_NAME);
				p.setProperty(Property.WEBAPP_ADDR, config.getWebAppAddr());
				System.getProperties().put(Property.WEBAPP_ADDR, config.getWebAppAddr());
				p.setProperty(Property.WEBAPP_PORT, config.getWebAppPort());
				System.getProperties().put(Property.WEBAPP_PORT, config.getWebAppPort());
				p.setProperty(Property.JNLP_RUNTIME_HOST, config.getJnlpRuntimeHost());
				p.setProperty(Property.JNLP_RUNTIME_FILE, config.getJnlpRuntimeFile());
				p.store(FileUtils.openOutputStream(toastProperties), null);
				if(isNewEnv){
					openConfigDialog();
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			String message = "$(user.home) system property not defined, application stopped !";
			LOG.error(message);
			throw new Error(message);
		}
	}

	private void downloadPlugins(final Config config) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try{
					Document doc = Jsoup.connect("http://" + config.getWebAppAddr() + ":8080/toast/agent-lib").get();
					for (Element file : doc.select("table")) {
						String outerHtml = file.outerHtml();
						Pattern p = Pattern.compile("<tt>([\\w-]+)\\.jar<\\/tt>");
						Matcher matcher = p.matcher(outerHtml);
						
						boolean foundJar = false;
						while(matcher.find()){
							foundJar = true;
							String fileName = matcher.group(1)+".jar";
							DownloadUtils.getFile("http://" + config.getWebAppAddr() + ":8080/toast/agent-lib/"+fileName,  config.getPluginDir());
						}
						if(!foundJar){
							throw new IllegalAccessError("URL doesn't contain jars! ");
						}
				    }
				}catch(Exception e){
					LOG.error("Couldn't download plugin and Agent at url " + "http://" + config.getWebAppAddr() + ":8080/toast/agent-lib" ,e);
					openConfigDialog();
				}						
			}
		});
	}

	@Override
	public void updateStatusMessage(String msg) {
		eventBus.post(new StatusMessage(msg));
	}

	@Override
	public void startProgress(String msg) {
		eventBus.post(new LoadingMessage(msg));
	}

	@Override
	public void updateProgress(String msg, int progress) {
		eventBus.post(new LoadingMessage(msg, progress));
	}

	@Override
	public void stopProgress(String msg) {
		eventBus.post(new StopLoadingMessage(msg));
	}

	@Override
	public boolean isConnected() {
		return serverClient.isConnected();
	}

	@Override
	public Config getConfig() {
		return this.config;
	}
	
	private void initProperties(File toastProperties) throws IOException {
		properties.load(FileUtils.openInputStream(toastProperties));
	}

	@Override
	public String getRuntimeType() {
		return (String) properties.get(Property.TOAST_RUNTIME_TYPE);
	}

	@Override
	public void openConfigDialog() {
		try {
			initProperties(toastPropertiesFile);
			//TODO: manage and hide/show + reload method
			final ConfigPanel configPanel = new ConfigPanel(properties, toastPropertiesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initProperties() {
		try {
			initProperties(toastPropertiesFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
