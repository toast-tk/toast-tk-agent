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

Creation date: 11 juin 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.swing.agent.runtime;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.synaptix.toast.constant.Property;
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
