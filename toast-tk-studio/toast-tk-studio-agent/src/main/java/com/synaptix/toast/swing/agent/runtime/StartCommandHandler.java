package com.synaptix.toast.swing.agent.runtime;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.config.ConfigProvider;

@FixMe(todo = "replace sysout with a logger")
public class StartCommandHandler {
	
	private static final Logger LOG = LogManager.getLogger(StartCommandHandler.class);
	private final Config configuration = new ConfigProvider().get();
	private Process process;
	private SutRunnerAsExec runner;

	public void start() {
		LOG.info("start command received !");
		if(process != null){
			stop();
			LOG.info("Stopping previous process !");
		}
		runner = SutRunnerAsExec.FromLocalConfiguration(configuration);
		process = runner.executeSutBat();
		LOG.info("new process started !");
	}

	public boolean init() {
		LOG.info("init command received !");
		try {
			if(runner == null){
				runner = SutRunnerAsExec.FromLocalConfiguration(configuration);
			}
			runner.init("JNLP", false);
			LOG.info("system initialized !");
			return true;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void stop() {
		if(this.process != null){
			LOG.info("Stoping process !");
			this.process.destroy();
			this.process = null;
			this.runner = null;
		}else{
			LOG.info("No Process to stop !");
		}
	}
	
}
