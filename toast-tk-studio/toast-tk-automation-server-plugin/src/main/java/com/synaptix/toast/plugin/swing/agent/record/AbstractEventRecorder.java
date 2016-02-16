package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fest.swing.input.InputState;

import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.guice.FilteredAWTEventListener;
import com.synaptix.toast.core.record.AwtEventProcessor;
import com.synaptix.toast.core.record.IEventRecorder;

/**
 * 
 * AWT event recorder.
 * Commonly shared methods between the differents records to build an AWTEventCapturedObject 
 *
 */
public abstract class AbstractEventRecorder implements FilteredAWTEventListener, AwtEventProcessor {

	private static final Logger LOG = LogManager.getLogger(AbstractEventRecorder.class);

	private InputState state;

	protected IEventRecorder eventRecorder;

	AbstractEventRecorder(
		final InputState state,
		final IEventRecorder eventRecorder) {
		this.state = state;
		this.eventRecorder = eventRecorder;
	}

	@Override
	public void eventDispatched(
		final AWTEvent event) {
		processEvent(event);
	}

	protected String getEventComponentLocator(
		AWTEvent awtEvent) {
		final Component component = getEventComponent(awtEvent);
		return eventRecorder.getComponentLocator(component);
	}

	private Component getEventComponent(
		final AWTEvent awtEvent) {
		Component component = awtEvent instanceof ComponentEvent ? ((ComponentEvent) awtEvent).getComponent() : null;
		if(component == null) {
			component = state.deepestComponentUnderMousePointer();
		}
		return component;
	}

	protected static String getEventValue(
		final AWTEvent awtEvent) {
		if(awtEvent instanceof KeyEvent) {
			return getKeyEvent(awtEvent);
		}
		else if(awtEvent instanceof MouseEvent) {
			return getMouseEvent(awtEvent);
		}
		else if(awtEvent instanceof FocusEvent) {
			return getFocusEvent(awtEvent);
		}
		return null;
	}

	private static String getFocusEvent(
		final AWTEvent awtEvent) {
		final FocusEvent fEvent = (FocusEvent) awtEvent;
		if(fEvent.getComponent() instanceof JCheckBox) {
			return getCheckBoxValue(fEvent);
		}
		if(fEvent.getComponent() instanceof JTextField) {
			return getTextFieldFocusEvent(fEvent);
		}
		if(fEvent.getComponent() instanceof JTextComponent) {
			return getTextComponentFocusEvent(fEvent);
		}
		if(fEvent.getComponent() instanceof JComboBox) {
			return getComboBoxFocusEvent(fEvent);
		}
		return null;
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	private static String getComboBoxFocusEvent(
		final FocusEvent focusEvent) {
		final Object selectedItem = ((JComboBox) focusEvent.getComponent()).getSelectedItem();
		final JList list = new JList(((JComboBox) focusEvent.getComponent()).getModel());
		final Component listCellRendererComponent = ((JComboBox) focusEvent.getComponent()).getRenderer()
			.getListCellRendererComponent(list, selectedItem, 0, false, false);
		if(selectedItem != null) {
			if(listCellRendererComponent instanceof JTextField) {
				return ((JTextField) listCellRendererComponent).getText();
			}
			else if(listCellRendererComponent instanceof JLabel) {
				return ((JLabel) listCellRendererComponent).getText();
			}
			else if(selectedItem instanceof String) {
				return selectedItem.toString();
			}
			else {
				return "unknowObjectType";
			}
		}
		return null;
	}

	private static String getTextComponentFocusEvent(
		final FocusEvent focusEvent) {
		return ((JTextComponent) focusEvent.getComponent()).getText();
	}

	private static String getTextFieldFocusEvent(
		final FocusEvent focusEvent) {
		return ((JTextField) focusEvent.getComponent()).getText();
	}

	private static String getCheckBoxValue(
		final FocusEvent focusEvent) {
		return Boolean.toString(!((JCheckBox) focusEvent.getComponent()).isSelected());
	}
	
	private static String getCheckBoxValue(
			final MouseEvent mEvent) {
		return Boolean.toString(!((JCheckBox) mEvent.getComponent()).isSelected());
	}


	private static String getMouseEvent(
		final AWTEvent awtEvent) {
		final MouseEvent mEvent = (MouseEvent) awtEvent;
		final Component componentEvent = mEvent.getComponent();
		if(componentEvent instanceof JTextField) {
			return getTextFieldMouseEvent(mEvent);
		}
		else if(componentEvent instanceof JTable) {
			return getJTableMouseEvent(mEvent);
		}
		else if(componentEvent instanceof JCheckBox) {
			return getCheckBoxValue(mEvent);
		}
		else if(componentEvent instanceof JList) {
			return getJListMouseEvent(mEvent);
		}
		return null;
	}

	private static String getTextFieldMouseEvent(
		final MouseEvent mouseEvent) {
		return ((JTextField) mouseEvent.getComponent()).getText();
	}

	private static String getJTableMouseEvent(
		final MouseEvent mouseEvent) {
		final JTable jSyTable = (JTable) mouseEvent.getComponent();
		final int selectedRowIndex = jSyTable.getSelectedRow();
		final int[] selectedColumns = jSyTable.getSelectedColumns();
		if(selectedColumns.length > 0) {
			final List<String> criteria = collectTableSelectionCriteria(jSyTable, selectedRowIndex, selectedColumns);
			return StringUtils.join(criteria, Property.TABLE_CRITERIA_SEPARATOR);
		}
		return "No Cell Selected";
	}

	private static List<String> collectTableSelectionCriteria(
		final JTable table,
		final int selectedRowIndex,
		final int[] selectedColumns) {
		final List<String> criteria = new ArrayList<String>();
		for(int columnIndex : selectedColumns) {
			final TableModel jTableModel = table.getModel();
			final String columnName = jTableModel.getColumnName(columnIndex);
			final Object cellValue = jTableModel.getValueAt(selectedRowIndex, columnIndex);
			criteria.add(columnName + Property.TABLE_KEY_VALUE_SEPARATOR + cellValue);
		}
		return criteria;
	}

	private static String getJListMouseEvent(
		final MouseEvent event) {
		final JList jList = (JList) event.getComponent();
		final int[] indices = jList.getSelectedIndices();
		final int lenght = indices != null ? indices.length : 0;
		if(lenght > 0) {
			final StringBuilder criteria = new StringBuilder();
			for(int index : indices) {
				criteria.append(index).append(Property.JLIST_CRITERIA_SEPARATOR);
			}
			return criteria.toString();
		}
		return "No Index Selected";
	}

	private static String getKeyEvent(
		final AWTEvent awtEvent) {
		final KeyEvent event = (KeyEvent) awtEvent;
		if(event.getID() == KeyEvent.KEY_RELEASED) {
			return Character.toString(event.getKeyChar());
		}
		return null;
	}

	protected String getEventComponentLabel(
		AWTEvent awtEvent) {
		Component component = getEventComponent(awtEvent);
		return getComponentName(component);
	}

	private static String computeMenuItemPath(List<String> namePath, JMenuItem menuItem){
		if(menuItem.getParent() instanceof JPopupMenu){
			JPopupMenu fromParent = (JPopupMenu)menuItem.getParent();
			if(fromParent.getInvoker() instanceof JMenu){
				JMenu menu = (JMenu)fromParent.getInvoker();
				if(menu != null){
					namePath.add(menu.getText());
				}
			}
			Collections.reverse(namePath);
			return StringUtils.join(namePath, " / ");
		}
		else if(menuItem.getParent() instanceof JMenuItem){
			namePath.add(((JMenuItem)menuItem.getParent()).getText());
			return computeMenuItemPath(namePath, (JMenuItem) menuItem.getParent());
		}else{
			Collections.reverse(namePath);
			return StringUtils.join(namePath, " / ");
		}
	}
	
	protected static String getComponentName(
		Component component) {
		if (component instanceof JMenuItem){
			final JMenuItem menuItem = (JMenuItem) component;
			final List<String> namePath = new ArrayList<String>();
			namePath.add(((JMenuItem)menuItem).getText());
			return computeMenuItemPath(namePath, menuItem);
		}
		else if(component instanceof AbstractButton) {
			final AbstractButton b = (AbstractButton) component;
			return b.getText();
		}
		return component != null ? component.getName() : null;
	}

	protected static String getEventComponentContainer(
		final AWTEvent event) {
		Component component = (Component) event.getSource();
		Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JDialog.class, component);
		String ancestorLocator = null;
		if(ancestorOfClass != null) {
			ancestorLocator = ((JDialog) ancestorOfClass).getTitle();
		}
		if(ancestorLocator == null) {
			ancestorLocator = getJTabbedPaneAncestor(component);
		}
		if(ancestorLocator == null) {
			ancestorLocator = getJLayeredPaneAncestor(component);
		}
		if(ancestorLocator == null) {
			ancestorLocator = getJFrameAncestor(component);
		}
		return ancestorLocator;
	}

	private static String getJFrameAncestor(
		Component component) {
		Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JFrame.class, component);
		String ancestorLocator = null;
		if(ancestorOfClass != null) {
			ancestorLocator = ((JFrame) ancestorOfClass).getTitle();
		}
		return ancestorLocator;
	}

	private static String getJTabbedPaneAncestor(
		Component component) {
		Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JTabbedPane.class, component);
		String ancestorLocator = null;
		if(ancestorOfClass != null) {
			ancestorLocator = ((JTabbedPane) ancestorOfClass).getTitleAt(((JTabbedPane) ancestorOfClass)
				.getSelectedIndex());
		}
		return ancestorLocator;
	}

	private static String getJLayeredPaneAncestor(
		Component component) {
		Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JLayeredPane.class, component);
		String ancestorLocator = null;
		if(ancestorOfClass != null) {
			ancestorLocator = ancestorOfClass.getClass().getSimpleName();
		}
		return ancestorLocator;
	}

	protected void appendEventRecord(
		final AWTCapturedEvent captureEvent) {
		LOG.info(
			"New record event captured: {}",
			ToStringBuilder.reflectionToString(captureEvent, ToStringStyle.SIMPLE_STYLE));
		eventRecorder.appendInfo(captureEvent);
	}

	protected static boolean isCapturedEventUninteresting(
		final AWTCapturedEvent captureEvent) {
		return captureEvent.businessValue == null && captureEvent.componentLocator == null;
	}
}