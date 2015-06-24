/**

Copyright (c) 2013-2015, Synaptix Labs
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

Creation date: 29 janv. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.constant;

public class Property {

	public static final String DEFAULT_WEBAPP_ADDR_PORT = "http://localhost:9000";
	public static final String WEBAPP_ADDR = "toast.webapp.addr";
	public static final String WEBAPP_PORT = "toast.webapp.port";
	public static final String TOAST_PLUGIN_DIR_PROP = "toast.plugin.dir";
	public static final String MONGO_HOST = "toast.mongo.addr";
	public static final String MONGO_PORT = "toast.mongo.port";
	
	public static final String TOAST_RUNTIME_TYPE = "toast.runtime.type";
	public static final String TOAST_RUNTIME_CMD = "toast.runtime.command";
	public static final String TOAST_RUNTIME_AGENT = "toast.runtime.agent";
	public static final String TOAST_HOME_DIR_NAME = ".toast";
	public static final String TOAST_HOME_DIR = System.getProperty("user.home") +  "\\"+TOAST_HOME_DIR_NAME+"\\";
	public static final String TOAST_PLUGIN_DIR = System.getProperty("user.home") + "\\"+TOAST_HOME_DIR_NAME+"\\plugins";
	public static final String TOAST_RUNTIME_DIR = System.getProperty("user.home") + "\\"+TOAST_HOME_DIR_NAME+"\\runtime";
	public static final String TOAST_LOG_DIR = System.getProperty("user.home") +  "\\"+TOAST_HOME_DIR_NAME+"\\log";;
	public static final String TOAST_PROPERTIES_FILE = Property.TOAST_HOME_DIR + "toast.properties";
	public static final String AGENT_JAR_NAME = "toast-tk-agent-standalone.jar";
	public static final String TOAST_SUT_RUNNER_BAT = "run_sut.bat";
	
	public static final String REDPEPPER_AUTOMATION_SETTINGS_DEFAULT_DIR = "settings/toast_descriptor.json";
	public static final String JNLP_RUNTIME_HOST = "toast.jnlp.runtime.host";
	public static final String JNLP_RUNTIME_FILE = "toast.jnlp.runtime.file";
	public static final String AGENT_DEBUG_AGRS = "toast.sut.debug.args";
	
	public static final String TABLE_CRITERIA_SEPARATOR = ";";
	public static final String TABLE_KEY_VALUE_SEPARATOR ="=";
	public static final String JLIST_CRITERIA_SEPARATOR = ";";
	public static final String DEFAULT_PARAM_SEPARATOR = ";";
	public static final String DEFAULT_PARAM_INPUT_SEPARATOR = "<-";
	public static final int TOAST_AGENT_PORT = 7676;
	
}
