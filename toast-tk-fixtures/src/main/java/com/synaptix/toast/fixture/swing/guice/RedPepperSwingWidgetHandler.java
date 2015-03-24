package com.synaptix.toast.fixture.swing.guice;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.MouseButton;
import org.fest.swing.data.TableCellByColumnId;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.automation.net.TableCommandRequest;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;
import com.synaptix.toast.fixture.utils.FestRobotInstance;

public class RedPepperSwingWidgetHandler implements ICustomFixtureHandler{

	private static final Logger LOG = LogManager.getLogger(RedPepperSwingWidgetHandler.class);
	public static final org.fest.swing.core.Robot rbt = FestRobotInstance.getRobot();
	
	@Override
	public String hanldeFixtureCall(Component target, IIdRequest request) {
		if(request instanceof CommandRequest){
			CommandRequest command = (CommandRequest) request;
			if (target instanceof JLabel) {
				handle((JLabel) target, command);
			} else if (target instanceof JTextField) {
				handle((JTextField) target, command);
			} else if (target instanceof JPasswordField) {
				handle((JPasswordField) target, command);
			} else if (target instanceof JButton) {
				handle((JButton) target, command);
			} else if (target instanceof JCheckBox) {
				handle((JCheckBox) target, command);
			} else if (target instanceof JTextArea) {
				handle((JTextArea) target, command);
			} else if (target instanceof JMenu) {
				handle((JMenu) target, command);
			} else if (target instanceof JTable) {
				if(command instanceof TableCommandRequest){
					return handle((JTable) target, (TableCommandRequest)command);
				}
				throw new IllegalAccessError("Command not supported for JTable Type :" + ToStringBuilder.reflectionToString(command, ToStringStyle.SHORT_PREFIX_STYLE));
			}else {
				LOG.debug("No Handle for swing component: " + ToStringBuilder.reflectionToString(target, ToStringStyle.SHORT_PREFIX_STYLE));
			}
		}else{
			LOG.debug("Unhandled swing request type: " + ToStringBuilder.reflectionToString(request, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		return null;
	}

	public void handle(JLabel label, CommandRequest command) {
		switch (command.action) {
		case SET:
			label.setText(command.value);
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for JLabel: " + command.action.name());
		}
	}
	
	public void handle(JTextField textField, CommandRequest command) {
		switch (command.action) {
		case SET:
			if ("date".equals(command.itemType)) {
				int value = Integer.parseInt(command.value);
				LocalDate date = LocalDate.now().plusDays(value);
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy");
				String formattedDate = formatter.print(date);
				textField.setText(formattedDate);
			} else {
				textField.setText(command.value);
			}
			break;
		case CLICK:
			rbt.click(textField);
		default:
			throw new IllegalArgumentException("Unsupported command for JTextField: " + command.action.name());
		}
	}

	public void handle(JPasswordField textField, CommandRequest command) {
		switch (command.action) {
		case SET:
			textField.setText(command.value);
			break;
		case CLICK:
			rbt.click(textField);
		default:
			throw new IllegalArgumentException("Unsupported command for JPasswordField: " + command.action.name());
		}
	}
	
	private void handle(JButton button, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			button.doClick();
		default:
			throw new IllegalArgumentException("Unsupported command for JButton: " + command.action.name());
		}
	}

	private void handle(JCheckBox checkbox, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			checkbox.doClick();
		default:
			throw new IllegalArgumentException("Unsupported command for JCheckBox: " + command.action.name());
		}
	}
	
	private void handle(JTextArea textField, CommandRequest command) {
		switch (command.action) {
		case SET:
			textField.setText(command.value);
			break;
		case CLICK:
			rbt.click(textField);
		case CLEAR:
			textField.setText("");
		default:
			throw new IllegalArgumentException("Unsupported command for JTextArea: " + command.action.name());
		}
	}


	private String handle(JTable target, final TableCommandRequest command) {
		JTableFixture tFixture = new JTableFixture(rbt, (JTable) target);
		switch (command.action) {
		case COUNT:
			int tries = 30;
			while (tFixture.rowCount() == 0 && tries > 0) {
				try {
					tries--;
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return String.valueOf(tFixture.rowCount());
		case FIND:
			TableCommandRequest tcommand = (TableCommandRequest) command;
			for (int i = 0; i < tFixture.rowCount(); i++) {
				JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.lookupCol));
				if (cell.value().equals(command.value)) {
					JTableCellFixture cell2 = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.resultCol));
					String value = cell2.value();
					cell2.select();
					return value;
				}
			}
			break;
		case DOUBLE_CLICK:
			tcommand = (TableCommandRequest) command;
			for (int i = 0; i < tFixture.rowCount(); i++) {
				JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.lookupCol));
				if (cell.value().equals(command.value)) {
					cell.select();
					cell.doubleClick();
				}
			}
			break;
		case SELECT_MENU:
			tcommand = (TableCommandRequest) command;
			for (int i = 0; i < tFixture.rowCount(); i++) {
				JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.lookupCol));
				if (cell.value().equals(tcommand.query.lookupValue)) {
					try {
						cell.select();
						cell.rightClick();
						JPopupMenuFixture pFixture = new JPopupMenuFixture(rbt, rbt.findActivePopupMenu());
						pFixture.menuItemWithPath(command.value).click();
					} catch (Exception e) {
						e.printStackTrace();
						return e.getMessage();
					}
				}
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JTable: " + command.action.name());
		}
		return null;
	}

	private void handle(JMenu target, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			target.doClick();
		case SELECT:
			if (target == null) { 
				rbt.pressMouse(MouseButton.RIGHT_BUTTON);
				JPopupMenuFixture pFixture = new JPopupMenuFixture(rbt, rbt.findActivePopupMenu());
				pFixture.menuItemWithPath(command.value).click();
			} else {
				target.doClick();
				JPopupMenuFixture pFixture = new JPopupMenuFixture(rbt, rbt.findActivePopupMenu());
				pFixture.menuItemWithPath(command.value).click();
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JMenu: " + command.action.name());
		}
	}

	@Override
	public Component locateComponentTarget(String item, String itemType, Component value) {
		return null; 
	}

	@Override
	public String processCustomCall(CommandRequest command) {
		return null;
	}
	
	@Override
	public String getName() {
		return "Toast-DefaultSwingWidgetHandler";
	}

	@Override
	public boolean isInterestedIn(Component component) {
		boolean isOk = component instanceof JLabel;
		isOk = isOk || component instanceof JTextField;
		isOk = isOk || component instanceof JPasswordField;
		isOk = isOk || component instanceof JButton;
		isOk = isOk || component instanceof JCheckBox;
		isOk = isOk || component instanceof JTextArea;
		isOk = isOk || component instanceof JMenu;
		isOk = isOk || component instanceof JTable;
		return isOk;
	}

	@SuppressWarnings("unchecked")
	static List<Class<? extends CommandRequest>> list = Collections.unmodifiableList(Arrays.asList(CommandRequest.class, TableCommandRequest.class));
	
	@Override
	public List<Class<? extends CommandRequest>> getCommandRequestWhiteList() {
		return list;
	}
}