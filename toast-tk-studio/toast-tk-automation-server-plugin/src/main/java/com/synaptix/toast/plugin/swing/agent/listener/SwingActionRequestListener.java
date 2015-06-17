package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.awt.Window;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPopupMenuFixture;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.response.ExistsResponse;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.core.report.TestResult.ResultKind;

/**
 * Created by Sallah Kokaina on 13/11/2014.
 */
public class SwingActionRequestListener extends Listener implements Runnable {

	private final static Logger LOG = LogManager.getLogger(SwingActionRequestListener.class);


	private final BlockingQueue<ActionRequestWrapper> actionRequestQueue;
	private final FixtureHandlerProvider fixtureHandlerProvider;
	private final ISynchronizationPoint synchronizationPoint;
	private final RepositoryHolder repositoryHolder;

	@Inject
	public SwingActionRequestListener(FixtureHandlerProvider fixtureHandlerProvider, 
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

		ActionRequestWrapper(CommandRequest request, Component target, Connection connection) {
			this.request = request;
			this.target = target;
			this.connection = connection;
		}
	}


	@Override
	public synchronized void received(Connection connection, Object object) {
		try {
			if (object instanceof CommandRequest) {
				LOG.info("Waiting for SUT to be ready !");
				checkSynchronizationPoint();
				
				LOG.info("Processing command {}", object);
				processReceivedCommand(connection, object);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void checkSynchronizationPoint() {
		if(synchronizationPoint != null){
			do{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}while(synchronizationPoint.hasToWait());
		}
	}
	
	private void processReceivedCommand(Connection connection, Object object) throws InterruptedException {
		CommandRequest command = (CommandRequest) object;
		if (command.isCustom()) {
			processCustomRequest(connection, object, command);
		} 
		else if(isComponentLessRequest(command)){
			processComponentLessRequest(command);
		}
		else {
			ResultKind response = processComponentRequest(connection, command);
			String id = ((CommandRequest) object).getId();
			if(id != null){
				connection.sendTCP(new ValueResponse(id, response != null ? response.name() : null));
			}
		}
	}

	private void processCustomRequest(Connection connection, Object object, CommandRequest command) {
		LOG.info("Processing custom command {}", object);
		String result = fixtureHandlerProvider.processCustomCall(command);
		if(command.getId() != null){
			connection.sendTCP(new ValueResponse(command.getId(), result));
		}
	}

	private void processComponentLessRequest(CommandRequest command) {
		switch (command.action) {
		case SET:
			FestRobotInstance.getRobot().enterText(command.value);
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for ComponentLess Requests: " + command.action);
		}
	}

	private boolean isComponentLessRequest(CommandRequest command) {
		return command.item == null && command.itemType == null;
	}

	private ResultKind processComponentRequest(Connection connection, CommandRequest command) throws InterruptedException {
		final Component target = getTarget(command.item, command.itemType);
		LOG.info("Found target command " + ToStringBuilder.reflectionToString(target, ToStringStyle.SHORT_PREFIX_STYLE));
		
		boolean componentFound = target != null;
		if (command.isExists()) {
			connection.sendTCP(new ExistsResponse(command.getId(), componentFound));
		} 
		else if (componentFound) {
			if(isToRunOutsideEDT(target)){
				handleActionOutsideEdt(target, command);
			}else{
				synchronized (actionRequestQueue) {
					actionRequestQueue.put(new ActionRequestWrapper(command, target, connection));
					SwingUtilities.invokeLater(SwingActionRequestListener.this);
				}
			}
		} 
		else if(command.itemType.equals(AutoSwingType.menu.name())){
			return handlePopupMenuItem(command);
		}
		else {
			LOG.error("No target found for command: " + ToStringBuilder.reflectionToString(command, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		return null;
	}
	
	private Component getTarget(String item, String itemType) {
		Component target = null;
		if("dialog".equalsIgnoreCase(itemType)){
			target = findContainerComponent(item, itemType);
		}else{
			target = findComponent(item, itemType);
		}
		return target;
	}

	private Component findContainerComponent(String item, String itemType) {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof JDialog) {
				JDialog dialog = (JDialog) window;
				if (dialog.isVisible() && dialog.getTitle().equalsIgnoreCase(item)) {
					return dialog;
				}
			}
		}
		return null;
	}

	private Component findComponent(String item, String itemType) {
		Map<String, Component> repository = repositoryHolder.getRepo();
		Component target = repository.get(item);
		if (target == null) {
			for (Map.Entry<String, Component> entrySet : repository.entrySet()) {
				Component value = entrySet.getValue();
				if (value.getClass().getSimpleName().toLowerCase().contains(itemType)) {
					if (value instanceof AbstractButton) {
						String buttonLabel = ((AbstractButton) value).getText();
						if (buttonLabel != null && buttonLabel.toLowerCase().equals(item.toLowerCase())) {
							target = value;
							break;
						}
					}
					else if (value instanceof JDialog){
						String dialogTitle = ((JDialog) value).getTitle();
						if (dialogTitle != null && dialogTitle.toLowerCase().equals(item.toLowerCase())) {
							target = value;
							break;
						}
					}
					else {
						target = fixtureHandlerProvider.locateComponentTarget(item, itemType, value);
						if (target != null) {
							return target;
						}
					}
				}
			}
		}
		return target;
	}

	private ResultKind handleActionOutsideEdt(Component target, CommandRequest command) {
		if (target instanceof JComboBox) {
			handle((JComboBox) target, command);
		}else if(target instanceof JMenu){
			return handle((JMenu)target, command);
		}
		return null;
	}

	private boolean isToRunOutsideEDT(Component target) {
		return (target instanceof JComboBox) || (target instanceof JMenu) /*|| (target instanceof JTextArea) */;
	}

	private ResultKind handlePopupMenuItem(CommandRequest command) {
		switch (command.action) {
		case SELECT:
			Robot robot = FestRobotInstance.getRobot();
			JPopupMenuFixture pFixture = new JPopupMenuFixture(robot, robot.findActivePopupMenu());
			JMenuItemFixture menuItemWithPath = pFixture.menuItemWithPath(command.value);
			if(menuItemWithPath != null && menuItemWithPath.component().isEnabled()){
				menuItemWithPath.click();
				return ResultKind.SUCCESS;
			}else{
				return ResultKind.FAILURE;
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JMenu: " + command.action.name());
		}
	}

	private String handle(JComboBox target, final CommandRequest command) {
		JComboBoxFixture fixture = new JComboBoxFixture(FestRobotInstance.getRobot(), target);
		switch (command.action) {
		case SET:
			fixture.focus().enterText(command.value);
			break;
		case GET:
			int selectedIndex = fixture.component().getSelectedIndex();
			return fixture.selectItem(selectedIndex).toString();
		case SELECT:
			if (StringUtils.isNumeric(command.value)) {
				fixture.selectItem(Integer.parseInt(command.value));
			} else {
				fixture.selectItem(command.value);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for JComboBox: " + command.action.name());
		}
		return null;
	}

	
	private ResultKind handle(JMenu target, CommandRequest command) {
		Robot robot = FestRobotInstance.getRobot();
		switch (command.action) {
		case CLICK:
			robot.click(target);
			LOG.info(String.format("Request id: %s, Clicked on menu (%s)", command.getId(), command.value));
			break;
		case SELECT:
			if (target == null) { 
				robot.pressMouse(MouseButton.RIGHT_BUTTON);
			} else {
				robot.click(target);
			}
			JPopupMenuFixture pFixture = new JPopupMenuFixture(robot, robot.findActivePopupMenu());
			JMenuItemFixture menuItemWithPath = pFixture.menuItemWithPath(command.value);
			if(menuItemWithPath != null && menuItemWithPath.component().isEnabled()){
				menuItemWithPath.click();
				LOG.info(String.format("Request id: %s, Selected menu (%s)", command.getId(), command.value));
				return ResultKind.SUCCESS;
			}else{
				return ResultKind.FAILURE;
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JMenu: " + command.action.name());
		}
		return null;
	}
	
	@Override
	public void run() {
		try {
			ActionRequestWrapper requestWrapper = actionRequestQueue.take();
			CommandRequest command = requestWrapper.request;
			Component target = requestWrapper.target;
			String response = fixtureHandlerProvider.processFixtureCall(target, command);
			if (response != null) {
				requestWrapper.connection.sendTCP(new ValueResponse(command.getId(), response));
				LOG.debug(String.format("Value (%s) sent to client for request id: (%s)", response, command.getId()));
			} else {
				LOG.debug(String.format("No response for request id: (%s)", command.getId()));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
