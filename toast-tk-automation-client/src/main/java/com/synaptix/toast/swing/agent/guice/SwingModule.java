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

Creation date: 16 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.swing.agent.guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.automation.config.Config;
import com.synaptix.toast.automation.config.ConfigProvider;
import com.synaptix.toast.automation.drivers.SwingInspectServerClient;
import com.synaptix.toast.core.inspection.ISwingInspectionClient;
import com.synaptix.toast.swing.agent.IToastClientApp;
import com.synaptix.toast.swing.agent.ToastApplication;
import com.synaptix.toast.swing.agent.interpret.MongoRepoManager;
import com.synaptix.toast.swing.agent.ui.SwingAgentScriptRunnerPanel;
import com.synaptix.toast.swing.agent.ui.SwingInspectionFrame;
import com.synaptix.toast.swing.agent.ui.SwingInspectionRecorderPanel;
import com.synaptix.toast.swing.agent.ui.SwingInspectorPanel;

public class SwingModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IToastClientApp.class).to(ToastApplication.class).asEagerSingleton();
		bind(SwingInspectionFrame.class).asEagerSingleton();

		bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
		
		bind(SwingAgentScriptRunnerPanel.class).in(Singleton.class);
		bind(SwingInspectorPanel.class).in(Singleton.class);
		bind(SwingInspectionRecorderPanel.class).in(Singleton.class);
		
		bind(ISwingInspectionClient.class).to(SwingInspectServerClient.class).in(Singleton.class);
		
		bind(MongoRepoManager.class).in(Singleton.class);
		bind(EventBus.class).in(Singleton.class);
	}
}
