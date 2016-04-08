package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.synaptix.toast.core.guice.ICustomRequestHandler;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.plugin.swing.server.SwingInspectionManager;

public class InitRequestListener extends Listener {

	private static final Logger LOG = LogManager.getLogger(InitRequestListener.class);

	private final RepositoryHolder repositoryHolder;

	static Set<Class<?>> authorizedComponents = new HashSet<>();

	static java.util.List<String> authorizedPackages = new ArrayList<>();

	static {
		authorizedComponents.add(JLabel.class);
		authorizedComponents.add(JButton.class);
		authorizedComponents.add(JRadioButton.class);
		authorizedComponents.add(JComboBox.class);
		authorizedComponents.add(JCheckBox.class);
		authorizedComponents.add(JTable.class);
		authorizedComponents.add(JList.class);
		authorizedComponents.add(JMenu.class);
		authorizedComponents.add(JMenuItem.class);
		authorizedComponents.add(JTextArea.class);
		authorizedComponents.add(JTextField.class);
		authorizedComponents.add(JPanel.class);
		authorizedComponents.add(JViewport.class);
		authorizedComponents.add(JPasswordField.class);
		authorizedPackages.add("com.synaptix.swing.simpledaystimeline");
		authorizedPackages.add("com.synaptix.swing");
	}

	@Inject
	public InitRequestListener(RepositoryHolder repositoryHolder, Set<ICustomRequestHandler> fixtureHandlers) {
		LOG.info("Initialize request listener. Action handlers " + fixtureHandlers.size());
		fixtureHandlers.stream().filter(fixtureHandler -> fixtureHandler.getComponentsWhiteList() != null).forEach(fixtureHandler -> {
			authorizedComponents.addAll(fixtureHandler.getComponentsWhiteList());
			for (Class<?> componentClass : fixtureHandler.getComponentsWhiteList()) {
				LOG.info("Adding component " + componentClass.getName() + " for fixture handler " + fixtureHandler.getName());
			}
		});
		this.repositoryHolder = repositoryHolder;
	}

	@Override
	public void received(
			Connection connection,
			Object object) {
		try {
			if (object instanceof InitInspectionRequest) {
				SwingInspectionManager.getInstance().clearContainers();
				mutateJDialogsModalityType();
				final List<Component> allComponents = SwingInspectionManager.getInstance().getAllComponents();
				final Map<Object, String> allInstances = SwingInspectionManager.getInstance().getAllInstances();
				repositoryHolder.getRepo().clear();
				for (Component component : allComponents) {
					String componentLocator = computeLocator(allInstances, component);
					if (isAuthorizedComponent(component)) {
						repositoryHolder.getRepo().put(componentLocator, component);
						if (component.getName() != null && !StringUtils.isEmpty(component.getName())) {
							repositoryHolder.getIdRepo().put(component.getName(), component);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private String computeLocator(
			Map<Object, String> allInstances,
			Component component) {
		String componentName = allInstances.get(component);
		String componentId = component.getName();
		String componentLocator = componentName != null ? componentName : componentId;
		componentLocator = componentLocator != null ? componentLocator : component.getClass() + ":"
				+ System.identityHashCode(component);
		return componentLocator;
	}

	private void mutateJDialogsModalityType() {
		for (Container f : getWindows()) {
			SwingInspectionManager.getInstance().addContainer(f);
			if (JDialog.class.isAssignableFrom(f.getClass())) {
				((JDialog) f).setModal(false);
				((JDialog) f).setModalityType(Dialog.ModalityType.MODELESS);
			}
		}
	}

	public static boolean isAuthorizedComponent(
			Component component) {
		for (String packageName : authorizedPackages) {
			if (component.getClass().getPackage().getName().startsWith(packageName)) {
				return true;
			}
		}
		if (authorizedComponents.contains(component.getClass())) {
			return true;
		}
		for (Class<?> autorizedComponent : authorizedComponents) {
			if (autorizedComponent.isAssignableFrom(component.getClass())) {
				return true;
			}
		}
		return false;
	}

	private static Container[] getWindows() {
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

	private static boolean acceptableContainer(
			Window w) {
		boolean scopable = !w.getClass().getPackage().getName().startsWith("fr.synaptix");
		boolean acceptable = scopable && (w instanceof Frame || w instanceof JDialog) && w.isVisible();
		return acceptable;
	}
}
