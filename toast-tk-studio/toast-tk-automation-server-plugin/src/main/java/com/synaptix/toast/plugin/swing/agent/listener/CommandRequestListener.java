package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.MouseButton;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JPopupMenuFixture;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.response.ExistsResponse;
import com.synaptix.toast.core.net.response.ValueResponse;

/**
 * Created by Sallah Kokaina on 13/11/2014.
 */
public class CommandRequestListener extends Listener implements Runnable {

	private final static Logger LOG = LogManager.getLogger(CommandRequestListener.class);

	private Map<String, Component> repository;
	private BlockingQueue<Work> queue;
	private final FixtureHandlerProvider fixtureHandlerProvider;

	@Inject
	public CommandRequestListener(FixtureHandlerProvider fixtureHandlerProvider) {
		super();
		this.queue = new ArrayBlockingQueue<Work>(1024);
		this.fixtureHandlerProvider = fixtureHandlerProvider;
	}

	public CommandRequestListener() {
		this(null);
	}

	class Work {
		public CommandRequest request;
		public Component target;
		private Connection connection;

		Work(CommandRequest request, Component target, Connection connection) {
			this.request = request;
			this.target = target;
			this.connection = connection;
		}
	}

	private Component getTarget(String item, String itemType) {
		Component target = repository.get(item);
		if (target == null) {
			for (Map.Entry<String, Component> entrySet : repository.entrySet()) {
				Component value = entrySet.getValue();
				if (value.getClass().getSimpleName().toLowerCase().contains(itemType)) {
					if (value instanceof AbstractButton) {
						if (((AbstractButton) value).getText() != null && ((AbstractButton) value).getText().toLowerCase().equals(item.toLowerCase())) {
							target = value;
							break;
						}
					} else {
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

	@Override
	public synchronized void received(Connection connection, Object object) {
		try {
			if (object instanceof CommandRequest) {
				LOG.info("Processing command {}", object);
				CommandRequest command = (CommandRequest) object;
				if (command.isCustom()) {
					LOG.info("Processing custom command {}", object);
					String result = fixtureHandlerProvider.processCustomCall(command);
					if(command.getId() != null){
						connection.sendTCP(new ValueResponse(command.getId(), result));
					}
				} else {
					Component target = getTarget(command.item, command.itemType);
					LOG.info("Found target command " + ToStringBuilder.reflectionToString(target, ToStringStyle.SHORT_PREFIX_STYLE));
					if (command.isExists()) {
						connection.sendTCP(new ExistsResponse(command.getId(), target != null));
					} else if (target != null) {
						// outside edt
						if (target instanceof JComboBox) {
							handle((JComboBox) target, command);
						}else if(target instanceof JMenu){
							handle((JMenu)target, command);
						}
						else {
							// within edt
							queue.put(new Work(command, target, connection));
							SwingUtilities.invokeLater(CommandRequestListener.this);
						}

					} 
					else if(command.itemType.equals(AutoSwingType.menu.name())){
						handlePopupMenuItem(command);
					}
					else {
						LOG.error("No target found for command: " + ToStringBuilder.reflectionToString(command, ToStringStyle.SHORT_PREFIX_STYLE));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handlePopupMenuItem(CommandRequest command) {
		switch (command.action) {
		case SELECT:
			JPopupMenuFixture pFixture = new JPopupMenuFixture(FestRobotInstance.getRobot(), FestRobotInstance.getRobot().findActivePopupMenu());
			pFixture.menuItemWithPath(command.value).click();
			break;
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

	
	private void handle(JMenu target, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			FestRobotInstance.getRobot().click(target);
			break;
		case SELECT:
			if (target == null) { 
				FestRobotInstance.getRobot().pressMouse(MouseButton.RIGHT_BUTTON);
				JPopupMenuFixture pFixture = new JPopupMenuFixture(FestRobotInstance.getRobot(), FestRobotInstance.getRobot().findActivePopupMenu());
				pFixture.menuItemWithPath(command.value).click();
			} else {
				FestRobotInstance.getRobot().click(target);
				JPopupMenuFixture pFixture = new JPopupMenuFixture(FestRobotInstance.getRobot(), FestRobotInstance.getRobot().findActivePopupMenu());
				pFixture.menuItemWithPath(command.value).click();
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for JMenu: " + command.action.name());
		}
	}
	@Override
	public void run() {
		try {
			Work work = queue.take();
			CommandRequest command = work.request;
			Component target = work.target;
			String response = fixtureHandlerProvider.processFixtureCall(target, command);
			if (response != null) {
				work.connection.sendTCP(new ValueResponse(command.getId(), response));
				LOG.debug(String.format("Value (%s) sent to client for request id: (%s)", response, command.getId()));
			} else {
				LOG.debug(String.format("No response for request id: (%s)", command.getId()));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// TODO create a singletonHolder
	public void setRepository(Map<String, Component> repository) {
		this.repository = repository;
	}

}
