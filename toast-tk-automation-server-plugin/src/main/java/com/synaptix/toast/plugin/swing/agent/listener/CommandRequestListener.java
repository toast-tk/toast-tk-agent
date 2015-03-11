package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fest.swing.fixture.JComboBoxFixture;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.ExistsResponse;
import com.synaptix.toast.automation.net.ValueResponse;
import com.synaptix.toast.fixture.utils.FestRobotInstance;

/**
 * Created by Sallah Kokaina on 13/11/2014.
 */
public class CommandRequestListener extends Listener implements Runnable {

	private final static Log LOG = LogFactory.getLog(CommandRequestListener.class);

	private Map<String, Component> repository;
	private BlockingQueue<Work> queue;
	private final FixtureHandlerProvider fixtureHandlerProvider;
	public static final org.fest.swing.core.Robot rbt = FestRobotInstance.getRobot();

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
				LOG.info("Processing command " + object.toString());
				CommandRequest command = (CommandRequest) object;
				if (command.isCustom()) {
					fixtureHandlerProvider.processCustomCall(command);
				} else {
					Component target = getTarget(command.item, command.itemType);
					LOG.info("Found target command " + ToStringBuilder.reflectionToString(target, ToStringStyle.SHORT_PREFIX_STYLE));
					if (command.isExists()) {
						connection.sendTCP(new ExistsResponse(command.getId(), target != null));
					} else if (target != null) {
						// outside edt
						if (target instanceof JComboBox) {
							handle((JComboBox) target, command);
						} else {
							// within edt
							queue.put(new Work(command, target, connection));
							SwingUtilities.invokeLater(CommandRequestListener.this);
						}

					} else {
						LOG.error("No target found for command: " + ToStringBuilder.reflectionToString(command, ToStringStyle.SHORT_PREFIX_STYLE));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handle(JComboBox target, final CommandRequest command) {
		JComboBoxFixture fixture = new JComboBoxFixture(rbt, target);
		switch (command.action) {
		case SET:
			fixture.focus().enterText(command.value);
			break;
		case SELECT:
			if (StringUtils.isNumeric(command.value)) {
				fixture.selectItem(Integer.parseInt(command.value));
			} else {
				fixture.selectItem(command.value);
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JComboBox: " + command.action.name());
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
