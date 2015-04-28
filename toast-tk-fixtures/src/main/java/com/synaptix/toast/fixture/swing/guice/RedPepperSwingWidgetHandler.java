package com.synaptix.toast.fixture.swing.guice;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.core.MouseButton;
import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCellByColumnId;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.automation.net.TableCommandRequest;
import com.synaptix.toast.automation.net.TableCommandRequestQueryCriteria;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;
import com.synaptix.toast.fixture.utils.FestRobotInstance;

public class RedPepperSwingWidgetHandler implements ICustomFixtureHandler{

	private static final Logger LOG = LogManager.getLogger(RedPepperSwingWidgetHandler.class);
	
	@Override
	public String hanldeFixtureCall(Component target, IIdRequest request) {
		if(request instanceof CommandRequest){
			CommandRequest command = (CommandRequest) request;
			if (target instanceof JLabel) {
				return handle((JLabel) target, command);
			} 
			else if (target instanceof JFormattedTextField) {
				return handle((JFormattedTextField) target, command);
			}
			else if (target instanceof JPasswordField) {
				return handle((JPasswordField) target, command);
			} 
			else if (target instanceof JTextField) {
				return handle((JTextField) target, command);
			} 
			else if (target instanceof JButton) {
				handle((JButton) target, command);
			} else if (target instanceof JCheckBox) {
				return handle((JCheckBox) target, command);
			} else if (target instanceof JTextArea) {
				return handle((JTextArea) target, command);
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

	public String handle(JLabel label, CommandRequest command) {
		switch (command.action) {
		case SET:
			label.setText(command.value);
			break;
		case GET:
			return label.getText();
		default:
			throw new IllegalArgumentException("Unsupported command for JLabel: " + command.action.name());
		}
		return null;
	}
	
	
	public String handle(final JFormattedTextField textField, final CommandRequest command) {
		switch (command.action) {
		case SET:
			if ("date".equals(command.itemType)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int value = Integer.parseInt(command.value);
						LocalDate date = LocalDate.now().plusDays(value);
						DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy");
						String formattedDate = formatter.print(date);
						
						
						/*ActionListener[] actionListeners = textField.getActionListeners();
						if(actionListeners != null) {
							for(final ActionListener actionListener :actionListeners)
							textField.removeActionListener(l)
						}*/
						
						textField.setText(formattedDate);
						try {
							textField.commitEdit();
						}catch(ParseException e) {
							e.printStackTrace();
						}
					}
				});
			}else if("date_text".equals(command.itemType)){
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						textField.setText(command.value);
						try {
							textField.commitEdit();
						}catch(ParseException e) {
							e.printStackTrace();
						}
					}
				});
			}else{
				textField.setText(command.value);
			}
			break;
		case CLICK:
			FestRobotInstance.getRobot().click(textField);
			break;
		case GET:
			return textField.getText();
		default:
			throw new IllegalArgumentException("Unsupported command for JTextField: " + command.action.name());
		}
		return null;
	}
	
	public String handle(final JTextField textField, final CommandRequest command) {
		switch (command.action) {
		case SET:
			if ("date".equals(command.itemType)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int value = Integer.parseInt(command.value);
						LocalDate date = LocalDate.now().plusDays(value);
						DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy");
						String formattedDate = formatter.print(date);
						textField.setText(formattedDate);
					}
				});
			}else if("date_text".equals(command.itemType)){
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						textField.setText(command.value);
					}
				});
			}else{
				textField.setText(command.value);
			}
			break;
		case CLICK:
			FestRobotInstance.getRobot().click(textField);
			break;
		case GET:
			return textField.getText();
		default:
			throw new IllegalArgumentException("Unsupported command for JTextField: " + command.action.name());
		}
		return null;
	}

	public String handle(JPasswordField textField, CommandRequest command) {
		//JTextComponentFixture tFixture = new JTextComponentFixture(FestRobotInstance.getRobot(), textField);
		switch (command.action) {
		case SET:
			textField.setText(command.value);
			break;
		case CLICK:
			FestRobotInstance.getRobot().click(textField);
			break;
		case GET:
			return StringUtils.join(textField.getPassword(), "");
		default:
			throw new IllegalArgumentException("Unsupported command for JPasswordField: " + command.action.name());
		}
		return null;
	}
	
	private void handle(final JButton button, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					button.doClick(1);					
				}
			});
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for JButton: " + command.action.name());
		}
	}

	private String handle(JCheckBox checkbox, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			JCheckBoxFixture bFixture = new JCheckBoxFixture(FestRobotInstance.getRobot(), checkbox);
			bFixture.click();
			break;
		case GET:
			return String.valueOf(checkbox.isSelected());
		default:
			throw new IllegalArgumentException("Unsupported command for JCheckBox: " + command.action.name());
		}
		return null;
	}
	
	private String handle(JTextArea textField, CommandRequest command) {

		JTextComponentFixture tFixture = new JTextComponentFixture(FestRobotInstance.getRobot(), textField);
		switch (command.action) {
		case SET:
			tFixture.setText(command.value);
			break;
		case GET:
			return tFixture.text();
		case CLICK:
			FestRobotInstance.getRobot().click(textField);
			break;
		case CLEAR:
			tFixture.setText(command.value);
		default:
			throw new IllegalArgumentException("Unsupported command for JTextArea: " + command.action.name());
		}
		return null;
	}


	private String handle(JTable target, final TableCommandRequest command) {
		JTableFixture tFixture = new JTableFixture(FestRobotInstance.getRobot(), (JTable) target);
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
			if(tcommand.query.criteria.size() == 0){
				return "No Criteria to select a row !";
			}
			if(tFixture.rowCount() == 0){
				return "The table is empty !";
			}
			for (int i = 0; i < tFixture.rowCount(); i++) {
				int totalFound = 0;
				boolean found = findRowByCriteria(tFixture, tcommand, i, totalFound);
				if(found){
					if(tcommand.query.resultCol != null){
						JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.resultCol));
						cell.select();
						return cell.value();
					}else{
						return String.valueOf((i+1));
					}
				}
			}
			return "No row matching provided criteria !";
		case DOUBLE_CLICK:
			tcommand = (TableCommandRequest) command;
			for (int i = 0; i < tFixture.rowCount(); i++) {
				int totalFound = 0;
				boolean found = findRowByCriteria(tFixture, tcommand, i, totalFound);
				if(found){
					JTableCellFixture cell = tFixture.cell(TableCell.row(i).column(1));
					cell.select();
					cell.doubleClick();
				}else{
					return "No row matching provided criteria !";
				}
			}
			break;
		case SELECT_MENU:
			tcommand = (TableCommandRequest) command;
			for (int i = 0; i < tFixture.rowCount(); i++) {
				int totalFound = 0;
				boolean found = findRowByCriteria(tFixture, tcommand, i, totalFound);
				if(found){
					JTableCellFixture cell = tFixture.cell(TableCell.row(i).column(1));
					try {
						cell.select();
						cell.rightClick();
						JPopupMenuFixture pFixture = new JPopupMenuFixture(FestRobotInstance.getRobot(), FestRobotInstance.getRobot().findActivePopupMenu());
						pFixture.menuItemWithPath(command.value).click();
					} catch (Exception e) {
						e.printStackTrace();
						return e.getMessage();
					}
				}else{
					return "No row matching provided criteria !";
				}
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported command for JTable: " + command.action.name());
		}
		return null;
	}

	private boolean findRowByCriteria(JTableFixture tFixture, TableCommandRequest tcommand, int i, int totalFound) {
		for(TableCommandRequestQueryCriteria criterion: tcommand.query.criteria){
			JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(criterion.lookupCol));
			if (cell.value().equals(criterion.lookupValue)) {
				totalFound++;
			}
			if(totalFound == tcommand.query.criteria.size()){
				return true;
			}
		}
		return false;
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

	static List<String> list = Collections.unmodifiableList(Arrays.asList(CommandRequest.class.getName(), TableCommandRequest.class.getName()));
	
	@Override
	public List<String> getCommandRequestWhiteList() {
		return list;
	}
}