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

package com.synaptix.toast.plugin.swing.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.FixtureHandlerProvider;
import com.synaptix.toast.plugin.swing.agent.listener.ISynchronizationPoint;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.RepositoryHolder;
import com.synaptix.toast.plugin.swing.agent.listener.SynchronizationPointImpl;
import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.plugin.swing.server.SwingInspectionServer;

public class SwingServerModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(RepositoryHolder.class).in(Singleton.class);
		bind(ISynchronizationPoint.class).to(SynchronizationPointImpl.class).in(Singleton.class);
		bind(ISwingInspectionServer.class).to(SwingInspectionServer.class).asEagerSingleton();
		bind(SwingActionRequestListener.class).in(Singleton.class);
		bind(InitRequestListener.class).in(Singleton.class);
		bind(IEventRecorder.class).to(SwingInspectionRecorder.class).in(Singleton.class);
		bind(FixtureHandlerProvider.class).in(Singleton.class);
	}
}
