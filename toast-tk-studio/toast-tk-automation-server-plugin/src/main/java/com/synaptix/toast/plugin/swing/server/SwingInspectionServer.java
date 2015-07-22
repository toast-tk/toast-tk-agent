package com.synaptix.toast.plugin.swing.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.inspection.CommonIOUtils;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.net.request.HighLightRequest;
import com.synaptix.toast.core.net.request.PoisonPill;
import com.synaptix.toast.core.net.request.RecordRequest;
import com.synaptix.toast.core.net.request.ScanRequest;
import com.synaptix.toast.core.net.response.RecordResponse;
import com.synaptix.toast.core.net.response.ScanResponse;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.RepositoryHolder;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;

/**
 * Created by skokaina on 07/11/2014.
 */
public class SwingInspectionServer implements ISwingInspectionServer {

	private static final Logger LOG = LogManager.getLogger(SwingInspectionServer.class);

	private List<Component> allComponents;

	private Map<Object, String> allInstances;

	final Server server;

	private final RepositoryHolder repositoryHolder;

	@Inject
	private IEventRecorder recorder;

	@Inject
	public SwingInspectionServer(
		SwingActionRequestListener commandRequestListener,
		InitRequestListener initRequestListener,
		RepositoryHolder repositoryHolder) {
		this.repositoryHolder = repositoryHolder;
		this.server = new Server(8192 * 1024, 8192 * 1024);
		try {
			CommonIOUtils.initSerialization(server.getKryo());
			this.server.start();
			this.server.bind(CommonIOUtils.TCP_PORT);
			initListeners(commandRequestListener, initRequestListener);
			LOG.info("Toast Inspection Server listening on port : " + CommonIOUtils.TCP_PORT);
		}
		catch(Exception e) {
			LOG.error("Server initialization error: " + e.getCause(), e);
			server.close();
		}
	}

	private void initListeners(
		SwingActionRequestListener commandRequestListener,
		InitRequestListener initRequestListener) {
		this.server.addListener(commandRequestListener);
		this.server.addListener(initRequestListener);
		initScanRequestListener();
		initHightLightRequestListener();
		initRecordRequestListener();
		initPoisonPillListener();
	}

	private void initPoisonPillListener() {
		this.server.addListener(new Listener() {

			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof PoisonPill) {
						System.exit(1);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initRecordRequestListener() {
		this.server.addListener(new Listener() {

			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof RecordRequest) {
						manageRecordRequest(((RecordRequest) object));
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initHightLightRequestListener() {
		this.server.addListener(new Listener() {

			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof HighLightRequest) {
						highlight(((HighLightRequest) object).getLocator());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initScanRequestListener() {
		this.server.addListener(new Listener() {

			@Override
			public void received(
				Connection connection,
				Object object) {
				try {
					if(object instanceof ScanRequest) {
						ScanRequest scanRequest = (ScanRequest) object;
						Set<String> components = scan(scanRequest.isDebug());
						ScanResponse response = new ScanResponse(scanRequest.getId(), components);
						connection.sendTCP(response);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void manageRecordRequest(
		RecordRequest recordRequest) {
		try {
			if(recordRequest.isStart()) {
				recorder.startRecording();
			}
			else {
				recorder.stopRecording();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getComponentLocator(
		Component component) {
		if(component != null) {
			String componentName = allInstances != null ? allInstances.get(component) : null;
			String componentId = component.getName();
			String componentLocator = componentName != null ? componentName : componentId;
			componentLocator = componentLocator != null ? componentLocator : component.getClass() + ":"
				+ System.identityHashCode(component);
			return componentLocator;
		}
		return null;
	}

	@Override
	public Set<String> scan(
		boolean debug) {
		SwingInspectionManager.getInstance().clearContainers();
		for(Container f : getWindows()) {
			SwingInspectionManager.getInstance().addContainer(f);
		}
		allComponents = SwingInspectionManager.getInstance().getAllComponents();
		allInstances = SwingInspectionManager.getInstance().getAllInstances();
		for(Component component : allComponents) {
			String componentName = allInstances.get(component);
			String componentId = component.getName();
			String componentLocator = componentName != null ? componentName : componentId;
			componentLocator = componentLocator != null ? componentLocator : component.getClass() + ":"
				+ System.identityHashCode(component);
			if(InitRequestListener.isAutorizedComponent(component)) {
				repositoryHolder.getRepo().put(componentLocator, component);
			}
		}
		return repositoryHolder.getRepo().keySet();
	}

	public static Container[] getWindows() {
		Window[] allWindows = Window.getWindows();
		int frameCount = 0;
		for(Window w : allWindows) {
			if(acceptableContainer(w)) {
				frameCount++;
			}
		}
		Container[] containers = new Container[frameCount];
		int c = 0;
		for(Window w : allWindows) {
			if(acceptableContainer(w)) {
				containers[c++] = (Container) w;
			}
		}
		return containers;
	}

	private static boolean acceptableContainer(
		Window w) {
		boolean scopable = !w.getClass().getPackage().getName().contains(".toast.");
		boolean acceptable = scopable && (w instanceof Frame || w instanceof JDialog) && w.isVisible();
		return acceptable;
	}

	Component previousComponent;

	Color previousColor;

	Boolean isMenuSelected = false;

	public synchronized void highlight(
		String selectComponent) {
		Component component = repositoryHolder.getRepo().get(selectComponent);
		if(component != null) {
			if(previousComponent != null) {
				revertComponentAppearance();
			}
			previousColor = component.getBackground();
			if(component instanceof JLabel) {
				((JLabel) component).setOpaque(true);
			}
			else if(component instanceof JMenu) {
				isMenuSelected = true;
				((JMenu) component).setSelected(isMenuSelected);
			}
			component.setBackground(Color.CYAN);
			previousComponent = component;
		}
	}

	private void revertComponentAppearance() {
		previousComponent.setBackground(previousColor);
		if(isMenuSelected && (previousComponent instanceof JMenu)) {
			isMenuSelected = false;
			((JMenu) previousComponent).setSelected(isMenuSelected);
		}
	}

	// TODO: implement
	// to publish
	// only to
	// relevant
	// client IDs
	// !!!
	@Override
	public void publishRecordEvent(
		AWTCapturedEvent eventObject) {
		server.sendToAllTCP(new RecordResponse(eventObject));
	}

	@Override
	public void publishInterpretedEvent(
		String sentence) {
		server.sendToAllTCP(new RecordResponse(sentence));
	}

	public void close() {
		server.close();
	}
}
