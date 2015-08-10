package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPopupMenuFixture;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.handler.ActionProcessor;
import com.synaptix.toast.adapter.swing.handler.ActionProcessorFactory;
import com.synaptix.toast.adapter.swing.handler.ActionProcessorFactoryProvider;
import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.response.ErrorResponse;
import com.synaptix.toast.core.net.response.ExistsResponse;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.core.report.TestResult.ResultKind;

public class SwingActionRequestListener extends Listener implements Runnable {

	private static final Logger LOG = LogManager.getLogger(SwingActionRequestListener.class);

	private final BlockingQueue<ActionRequestWrapper> actionRequestQueue;

	private final FixtureHandlerProvider fixtureHandlerProvider;

	private final ISynchronizationPoint synchronizationPoint;

	private final RepositoryHolder repositoryHolder;

	@Inject
	public SwingActionRequestListener(
		FixtureHandlerProvider fixtureHandlerProvider,
		ISynchronizationPoint synchronizationPoint,
		RepositoryHolder repositoryHolder) {
		super();
		this.actionRequestQueue = new ArrayBlockingQueue<ActionRequestWrapper>(1024);
		this.fixtureHandlerProvider = fixtureHandlerProvider;
		this.synchronizationPoint = synchronizationPoint;
		this.repositoryHolder = repositoryHolder;
	}

	class ActionRequestWrapper {

		public CommandRequest request;

		public Component target;

		private Connection connection;

		ActionRequestWrapper(
			CommandRequest request,
			Component target,
			Connection connection) {
			this.request = request;
			this.target = target;
			this.connection = connection;
		}
	}

	@Override
	public synchronized void received(
		Connection connection,
		Object object) {
		try {
			if(object instanceof CommandRequest) {
				LOG.info("Waiting for SUT to be ready !");
				checkSynchronizationPoint();
				LOG.info("Processing command {}", object);
				processReceivedCommand(connection, object);
			}
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
// if (object instanceof CommandRequest) {
// failSafeAndReportSceenshot(connection, e.getMessage(), (CommandRequest)
// object);
// }
		}
	}

	private void checkSynchronizationPoint() {
		if(synchronizationPoint != null) {
			do {
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			while(synchronizationPoint.hasToWait());
		}
	}

	private void processReceivedCommand(
		Connection connection,
		Object object)
		throws InterruptedException, AWTException {
		CommandRequest command = (CommandRequest) object;
		if(command.isCustom()) {
			processCustomRequest(connection, object, command);
		}
		else if(isComponentLessRequest(command)) {
			processComponentLessRequest(command);
		}
		else {
			processComponentRequest(connection, command);
		}
	}

	private void processCustomRequest(
		Connection connection,
		Object object,
		CommandRequest command)
		throws AWTException {
		LOG.info("Processing custom command {}", object);
		try {
			String result = fixtureHandlerProvider.processCustomCall(command);
			if(command.getId() != null) {
				BufferedImage capture = getDesktopScreenShot();
				connection.sendTCP(new ValueResponse(command.getId(), result));
			}
		}
		catch(IllegalAccessException nfe) {
			// FixMe send ErrorResponse
			BufferedImage capture = getDesktopScreenShot();
			connection.sendTCP(new ValueResponse(command.getId(), "No Inplementation found"));
			LOG.error(nfe.getMessage(), nfe);
		}
	}

// private void failSafeAndReportSceenshot(Connection connection, String
// message, CommandRequest command) {
// try {
// BufferedImage capture = getDesktopScreenShot();
// connection.sendTCP(new ErrorResponse(command.getId(), message, capture));
// } catch (AWTException e) {
// LOG.error(e.getMessage(), e);
// connection.sendTCP(new ErrorResponse(command.getId(), message, null));
// }
// }
	private BufferedImage getDesktopScreenShot()
		throws AWTException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = new java.awt.Robot().createScreenCapture(screenRect);
		return capture;
	}

	private void processComponentLessRequest(
		CommandRequest command) {
		switch(command.action) {
			case SET :
				FestRobotInstance.getRobot().enterText(command.value);
				break;
			default :
				throw new IllegalArgumentException("Unsupported command for ComponentLess Requests: " + command.action);
		}
	}

	private boolean isComponentLessRequest(
		CommandRequest command) {
		return command.item == null && command.itemType == null;
	}

	private void processComponentRequest(
		Connection connection,
		CommandRequest command)
		throws InterruptedException, AWTException {
		final Component target = getTarget(command.item, command.itemType);
		LOG.info("Found target command " + ToStringBuilder.reflectionToString(target, ToStringStyle.SHORT_PREFIX_STYLE));
		boolean componentFound = target != null;
		if(command.isExists()) {
			replyToComponentExistRequest(connection, command, componentFound);
		}
		else if(componentFound) {
			doActionOnComponent(connection, command, target);
		}
		else if(command.itemType.equals(AutoSwingType.menu.name())) {
			doActionOnMenuByNameLocator(connection, command);
		}
		else {
			LOG.error("No target found for command: "
				+ ToStringBuilder.reflectionToString(command, ToStringStyle.SHORT_PREFIX_STYLE));
			// failSafeAndReportSceenshot(connection, "No target found !",
// command);
		}
	}

	private void doActionOnMenuByNameLocator(
		Connection connection,
		CommandRequest command)
		throws AWTException {
		ResultKind result;
		result = handlePopupMenuItem(command);
		if(command.getId() != null) {
			if(result != null) {
				if(result.equals(ResultKind.ERROR) || result.equals(ResultKind.FAILURE)) {
					// failSafeAndReportSceenshot(connection, "", command);
				}
				else {
				}
				BufferedImage capture = getDesktopScreenShot();
				connection.sendTCP(new ValueResponse(command.getId(), result != null ? result.name() : null));
			}
			else {
				BufferedImage capture = getDesktopScreenShot();
				connection.sendTCP(new ValueResponse(command.getId(), null));
			}
		}
	}

	private void replyToComponentExistRequest(
		Connection connection,
		CommandRequest command,
		boolean componentFound) {
		connection.sendTCP(new ExistsResponse(command.getId(), componentFound));
	}

	private void doActionOnComponent(
		Connection connection,
		CommandRequest command,
		final Component target)
		throws InterruptedException {
		if(isToRunOutsideEdt(target)) {
			handleActionOutsideEdt(target, command);
		}
		else {
			queueAndHandleInsideEdt(connection, command, target);
		}
	}

	private void queueAndHandleInsideEdt(
		Connection connection,
		CommandRequest command,
		final Component target)
		throws InterruptedException {
		actionRequestQueue.put(new ActionRequestWrapper(command, target, connection));
		SwingUtilities.invokeLater(SwingActionRequestListener.this);
	}

	private Component getTarget(
		String item,
		String itemType) {
		Component target = null;
		if("dialog".equalsIgnoreCase(itemType)) {
			target = findContainerComponent(item, itemType);
		}
		else {
			target = findComponent(item, itemType);
		}
		return target;
	}

	private Component findContainerComponent(
		String item,
		String itemType) {
		Window[] windows = Window.getWindows();
		for(Window window : windows) {
			if(window instanceof JDialog) {
				JDialog dialog = (JDialog) window;
				if(dialog.isVisible() && dialog.getTitle().equalsIgnoreCase(item)) {
					return dialog;
				}
			}
		}
		return null;
	}

	private Component findComponent(
		String item,
		String itemType) {
		Component target = repositoryHolder.getRepo().get(item);
		if(target == null) {
			for(Map.Entry<String, Component> entrySet : repositoryHolder.getRepo().entrySet()) {
				final Component component = entrySet.getValue();
				if(isComponentTypeMatching(itemType, component)) {
					target = findComponentByItemName(item, itemType, component);
					if(target != null) {
						return target;
					}
				}
			}
		}
		return target;
	}

	private Component findComponentByItemName(
		String item,
		String itemType,
		final Component component) {
		Component foundComponent = null;
		if(component instanceof AbstractButton) {
			String buttonLabel = ((AbstractButton) component).getText();
			if(buttonLabel != null && buttonLabel.toLowerCase().equals(item.toLowerCase())) {
				foundComponent = component;
			}
		}
		else if(component instanceof JDialog) {
			String dialogTitle = ((JDialog) component).getTitle();
			if(dialogTitle != null && dialogTitle.toLowerCase().equals(item.toLowerCase())) {
				foundComponent = component;
			}
		}
		else {
			final Component locatedTargetComponent = fixtureHandlerProvider.locateComponentTarget(
				item,
				itemType,
				component);
			if(locatedTargetComponent != null) {
				foundComponent = locatedTargetComponent;
			}
		}
		return foundComponent;
	}

	private boolean isComponentTypeMatching(
		String itemType,
		Component value) {
		return value.getClass().getSimpleName().toLowerCase().contains(itemType);
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private ResultKind handleActionOutsideEdt(
		Component target,
		CommandRequest command) {
		ActionProcessorFactory factory = ActionProcessorFactoryProvider.getFactory(target);
		ActionProcessor actionProcessor = factory.getProcessor(command);
		if(actionProcessor == null) {
			throw new IllegalArgumentException(String.format(
				"Command not supported for %s: " + command.action.name(),
				target.getClass().getName()));
		}
		String output = actionProcessor.processCommandOnComponent(command, target);
		return output == null ? null : ResultKind.valueOf(output);
	}

	private boolean isToRunOutsideEdt(
		Component target) {
		return (target instanceof JComboBox) || (target instanceof JMenu);
	}

	private ResultKind handlePopupMenuItem(
		CommandRequest command) {
		switch(command.action) {
			case SELECT :
				Robot robot = FestRobotInstance.getRobot();
				JPopupMenuFixture popupFixture = new JPopupMenuFixture(robot, robot.findActivePopupMenu());
				JMenuItemFixture menuItemWithPath = popupFixture.menuItemWithPath(command.value);
				if(menuItemWithPath != null && menuItemWithPath.component().isEnabled()) {
					menuItemWithPath.click();
					return ResultKind.SUCCESS;
				}
				else {
					return ResultKind.FAILURE;
				}
			default :
				throw new IllegalArgumentException("Unsupported command for JMenu: " + command.action.name());
		}
	}

	@Override
	public void run() {
		try {
			ActionRequestWrapper requestWrapper = actionRequestQueue.take();
			CommandRequest command = requestWrapper.request;
			Component target = requestWrapper.target;
			String response = fixtureHandlerProvider.processFixtureCall(target, command);
			if(response != null) {
				requestWrapper.connection.sendTCP(new ValueResponse(command.getId(), response));
				LOG.debug(String.format("Value (%s) sent to client for request id: (%s)", response, command.getId()));
			}
			else {
				LOG.debug(String.format("No response for request id: (%s)", command.getId()));
			}
		}
		catch(InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
