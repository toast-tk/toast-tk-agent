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

import org.xml.sax.SAXException;

import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.config.ConfigProvider;

@FixMe(todo = "replace sysout with a logger")
public class StartCommandHandler {
	Config configuration = new ConfigProvider().get();
	Process process;
	SutRunnerAsExec runner;

	public void start() {
		System.out.println("start command received !");
		if(process != null){
			stop();
			System.out.println("Stopping previous process !");
		}
		runner = new SutRunnerAsExec(configuration);
		process = runner.doRemoteAppRun(Property.TOAST_HOME_DIR + Property.TOAST_SUT_RUNNER_BAT);
		System.out.println("new process started !");
	}

	public boolean init() {
		System.out.println("init command received !");
		try {
			if(runner == null){
				runner = new SutRunnerAsExec(configuration);
			}
			runner.init("JNLP", Property.TOAST_RUNTIME_AGENT + "\\toast-tk-agent-standalone.jar" , false);
			System.out.println("system initialized !");
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
			System.out.println("Stoping process !");
			this.process.destroy();
			this.process = null;
			this.runner = null;
		}else{
			System.out.println("No Process to stop !");
		}
	}
	
}
