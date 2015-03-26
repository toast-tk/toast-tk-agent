package com.synaptix.toast.plugin.swing.server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.inject.Inject;
import com.synaptix.toast.automation.net.HighLightRequest;
import com.synaptix.toast.automation.net.PoisonPill;
import com.synaptix.toast.automation.net.RecordRequest;
import com.synaptix.toast.automation.net.RecordResponse;
import com.synaptix.toast.automation.net.ScanRequest;
import com.synaptix.toast.automation.net.ScanResponse;
import com.synaptix.toast.core.inspection.CommonIOUtils;
import com.synaptix.toast.core.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.interpret.EventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.listener.CommandRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;

/**
 * Created by skokaina on 07/11/2014.
 */
public class SwingInspectionServer implements ISwingInspectionServer {

	Logger LOG = Logger.getLogger(SwingInspectionServer.class.getName());
	private final Map<String, Component> repository;
	private List<Component> allComponents;
	private Map<Object, String> allInstances;
	final Server server;

	@Inject
	private IEventRecorder recorder;

	@Inject
	public SwingInspectionServer(CommandRequestListener commandRequestListener, InitRequestListener initRequestListener) {
		repository = new ConcurrentHashMap<String, Component>();
		server = new Server(8192 * 8192, 8192 * 8192);
		try {
			CommonIOUtils.initSerialization(server.getKryo());
			server.start();
			server.bind(CommonIOUtils.TCP_PORT);
			commandRequestListener.setRepository(repository);
			initRequestListener.setRepository(repository);

			server.addListener(new Listener.ThreadedListener(commandRequestListener));
			server.addListener(new Listener.ThreadedListener(initRequestListener));

			server.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {
					try {
						if (object instanceof ScanRequest) {
							ScanRequest scanRequest = (ScanRequest) object;
							List<String> components = scan(scanRequest.isDebug());
							ScanResponse response = new ScanResponse(scanRequest.getId(), components);
							connection.sendTCP(response);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			server.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {
					try {
						if (object instanceof HighLightRequest) {
							highlight(((HighLightRequest) object).getLocator());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			server.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {
					try {
						if (object instanceof RecordRequest) {
							manageRecordRequest(((RecordRequest) object));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			server.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {
					try {
						if (object instanceof PoisonPill) {
							System.exit(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			LOG.info("Inspection Server listening on port : " + CommonIOUtils.TCP_PORT);
		} catch (Exception e) {
			LOG.info("Server initialization error: " + e.getCause());
			e.printStackTrace();
			server.close();
		}
	}

	protected void manageRecordRequest(RecordRequest recordRequest) {
		try {
			if (recordRequest.isStart()) {
				recorder.startRecording();
			} else {
				recorder.stopRecording();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SwingInspectionServer() {
		this(new CommandRequestListener(), new InitRequestListener());
	}

	public String getComponentLocator(Component component) {
		if (component != null) {
			String componentName = allInstances != null ? allInstances.get(component) : null;
			String componentId = component.getName();
			String componentLocator = componentName != null ? componentName : componentId;
			componentLocator = componentLocator != null ? componentLocator : component.getClass() + ":" + System.identityHashCode(component);
			return componentLocator;
		}
		return null;
	}

	@Override
	public List<String> scan(boolean debug) {
		List<String> components = new ArrayList<String>();
		SwingInspectionManager.getInstance().clearContainers();
		for (Container f : getWindows()) {
			SwingInspectionManager.getInstance().addContainer(f);
		}

		allComponents = SwingInspectionManager.getInstance().getAllComponents();
		allInstances = SwingInspectionManager.getInstance().getAllInstances();

		for (Component component : allComponents) {
			String componentName = allInstances.get(component);
			String componentId = component.getName();
			String componentLocator = componentName != null ? componentName : componentId;
			componentLocator = componentLocator != null ? componentLocator : component.getClass() + ":" + System.identityHashCode(component);
			if (InitRequestListener.isAutorizedComponent(component)) {
				components.add(componentLocator);
				repository.put(componentLocator, component);
			}
		}
		return components;
	}

	public static Container[] getWindows() {
		Window[] allWindows = Window.getWindows();

		int frameCount = 0;
		for (Window w : allWindows) {
			if (acceptableContainer(w)) {
				frameCount++;
			}
		}

		Container[] containers = new Container[frameCount];
		int c = 0;
		for (Window w : allWindows) {
			if (acceptableContainer(w)) {
				containers[c++] = (Container) w;
			}
		}

		return containers;
	}

	private static boolean acceptableContainer(Window w) {
		boolean scopable = !w.getClass().getPackage().getName().contains(".redpepper.");
		boolean acceptable = scopable && (w instanceof Frame || w instanceof JDialog) && w.isVisible();
		return acceptable;
	}

	Component previousComponent;
	Color previousColor;
	Boolean isMenuSelected = false;

	public synchronized void highlight(String selectComponent) {
		Component component = repository.get(selectComponent);
		if (component != null) {

			if (previousComponent != null) {
				previousComponent.setBackground(previousColor);
				if (isMenuSelected && (previousComponent instanceof JMenu)) {
					isMenuSelected = false;
					((JMenu) previousComponent).setSelected(isMenuSelected);
				}
			}

			previousColor = component.getBackground();
			if (component instanceof JLabel) {
				((JLabel) component).setOpaque(true);
			} else if (component instanceof JMenu) {
				isMenuSelected = true;
				((JMenu) component).setSelected(isMenuSelected);
			}
			component.setBackground(Color.CYAN);

			previousComponent = component;
		}
	}

	// TODO: implement
	// to publish
	// only to
	// relevant
	// client IDs
	// !!!
	@Override
	public void publishRecordEvent(EventCapturedObject eventObject) {
		server.sendToAllTCP(new RecordResponse(eventObject)); 
	}

	@Override
	public void publishInterpretedEvent(String sentence) {
		server.sendToAllTCP(new RecordResponse(sentence));
	}

	public void close() {
		server.close();
	}
	
}
