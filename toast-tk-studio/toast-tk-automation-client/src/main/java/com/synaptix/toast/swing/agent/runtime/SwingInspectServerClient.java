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

Creation date: 18 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.swing.agent.runtime;

import java.io.IOException;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.synaptix.toast.automation.driver.swing.SwingClientDriver;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionClient;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter;
import com.synaptix.toast.core.agent.interpret.InterpretedEvent;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.HighLightRequest;
import com.synaptix.toast.core.net.request.IIdRequest;
import com.synaptix.toast.core.net.request.PoisonPill;
import com.synaptix.toast.core.net.request.RecordRequest;
import com.synaptix.toast.core.net.request.ScanRequest;
import com.synaptix.toast.core.net.response.RecordResponse;
import com.synaptix.toast.core.net.response.ScanResponse;
import com.synaptix.toast.core.runtime.ITCPResponseReceivedHandler;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.interpret.LiveRedPlayEventInterpreter;
import com.synaptix.toast.swing.agent.interpret.MongoRepoManager;

public class SwingInspectServerClient extends SwingClientDriver implements ISwingInspectionClient {

	private EventBus eventBus;

	private String previousInput;

	IEventInterpreter interpreter;

	private static final Logger LOG = LogManager.getLogger(SwingInspectServerClient.class);
	
	public SwingInspectServerClient(String host) throws IOException {
		super(host);
	}

	@Inject
	public SwingInspectServerClient(final EventBus eventBus, final MongoRepoManager mongoRepoManager) throws IOException {
		this("localhost");
		this.eventBus = eventBus;
		client.addConnectionHandler(new ITCPResponseReceivedHandler(){
			@Override
			public void onResponseReceived(Object object) {
				eventBus.post(new SeverStatusMessage(SeverStatusMessage.State.CONNECTED));
			}
		});
		client.addDisconnectionHandler(new ITCPResponseReceivedHandler(){
			@Override
			public void onResponseReceived(Object object) {
				eventBus.post(new SeverStatusMessage(SeverStatusMessage.State.DISCONNECTED));
				startConnectionLoop();						
			}
		});

		this.interpreter = new LiveRedPlayEventInterpreter(mongoRepoManager);
	}

	@Override
	public void highlight(String selectedValue) {
		process(new HighLightRequest(selectedValue));
	}

	@Override
	public void scanUi(final boolean selected) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final String requestId = UUID.randomUUID().toString();
				ScanRequest scanRequest = new ScanRequest(requestId, selected);
				client.sendRequest(scanRequest);
			}
		});
	}

	@Override
	protected void handleResponse(IIdRequest response) {
		if (response instanceof ScanResponse) {
			eventBus.post((ScanResponse) response);
		} else if (response instanceof RecordResponse) {
			RecordResponse result = (RecordResponse) response;
			if (result.getSentence() != null) {
				eventBus.post(new InterpretedEvent(result.getSentence()));
			} 
			else 
			{
				String command = buildFormat(result);
				if (command != null && !command.equals(previousInput)) {
					eventBus.post(new InterpretedEvent(command, result.value.timeStamp));
				}
				previousInput = command;
			}
		}
	}

	@Override
	public void startRecording() {
		client.sendRequest(new RecordRequest(true));
	}

	@Override
	public void stopRecording() {
		client.sendRequest(new RecordRequest(false));
	}

	@Override
	public void setMode(int mode) {
//		if (mode == 0) {
//			this.interpreter = new LiveRedPlayEventInterpreter();
//		} else {
//			this.interpreter = new DefaultEventInterpreter();
//		}
	}

	private String buildFormat(RecordResponse response) {
		switch (response.value.getEventType()) {
		case BUTTON_CLICK:
			return interpreter.onButtonClick(response.value);
		case CHECKBOX_CLICK:
			return interpreter.onCheckBoxClick(response.value);
		case CLICK:
			return interpreter.onClick(response.value);
		case TABLE_CLICK:
			return interpreter.onTableClick(response.value);
		case MENU_CLICK:
			return interpreter.onMenuClick(response.value);
		case COMBOBOX_CLICK:
			return interpreter.onComboBoxClick(response.value);
		case WINDOW_DISPLAY:
			return interpreter.onWindowDisplay(response.value);
		case KEY_INPUT:
			return interpreter.onKeyInput(response.value);
		case BRING_ON_TOP_DISPLAY:
			return interpreter.onBringOnTop(response.value);
		case POPUP_MENU_CLICK:
			return interpreter.onPopupMenuClick(response.value);
		default:
			return "unhandled event interpretation !";
		}
	}

	@Override
	public void processCustomCommand(String command) {
		CommandRequest request = new CommandRequest.CommandRequestBuilder(null).asCustomCommand(command).build();
		client.sendRequest(request);
	}

	public void processCustomCommand(CommandRequest request) {
		client.sendRequest(request);
	}

	@Override
	public void killServer() {
		LOG.info("Terminating inspection server - Poison Pill !");
		client.sendRequest(new PoisonPill());
	}

	@Override
	public boolean saveObjectsToRepository() {
		if(interpreter instanceof LiveRedPlayEventInterpreter){
			return ((LiveRedPlayEventInterpreter)interpreter).saveObjectsToRepository();
		}else{
			LOG.info("Current interpreter doesn't support repository update operation: " + interpreter.getClass().getSimpleName());
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		return client.isConnected();
	}

	@Override
	public boolean isConnectedToWebApp() {
		return interpreter.isConnectedToWebApp();
	}

	@Override
	public String processAndwaitForValue(String requestId) {
		// TODO Auto-generated method stub
		return null;
	}

}
