package com.synaptix.toast.swing.agent.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.synaptix.toast.core.agent.inspection.CommonIOUtils;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.request.PoisonPill;
import com.synaptix.toast.core.net.response.WebRecordResponse;

public class KryoAgentServer implements IAgentServer{

	private static final Logger LOG = LogManager.getLogger(KryoAgentServer.class);
	private final Server server;
	private final RestRecorderService restRecorderService;

	public KryoAgentServer(RestRecorderService restRecorderService) {
		this.server = new Server(8192 * 1024, 8192 * 1024);
		this.restRecorderService = restRecorderService;
		try {
			CommonIOUtils.initSerialization(server.getKryo());
			this.server.start();
			this.server.bind(CommonIOUtils.AGENT_TCP_PORT);
			initRecordListener();
			LOG.info("Toast Agent Server listening on port : " + CommonIOUtils.AGENT_TCP_PORT);
		}
		catch(Exception e) {
			LOG.error("Server initialization error: " + e.getCause(), e);
			server.close();
		}
	}

	private void initRecordListener() {
		this.server.addListener(new Listener(){
			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof InitInspectionRequest) {
						LOG.info("receiving init request !");
						InitInspectionRequest request = (InitInspectionRequest) object;
						restRecorderService.openRecordingBrowser(request.text);
					}
				}
				catch(Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		});
		this.server.addListener(new Listener() {
			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof PoisonPill) {
						restRecorderService.stop();
						restRecorderService.closeBrowser();
						System.exit(1);
					}
				}
				catch(Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		});
	}

	public void close() {
		server.close();
	}

	public void sendEvent(
		WebEventRecord record) {
		this.server.sendToAllTCP(new WebRecordResponse(record));
	}
}
