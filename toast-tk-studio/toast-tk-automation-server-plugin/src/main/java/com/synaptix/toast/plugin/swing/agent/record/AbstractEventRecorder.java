/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 6 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
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
import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.guice.FilteredAWTEventListener;
import com.synaptix.toast.core.record.AwtEventProcessor;
import com.synaptix.toast.core.record.IEventRecorder;

public abstract class AbstractEventRecorder implements FilteredAWTEventListener, AwtEventProcessor {

	private static final Logger LOG = LogManager.getLogger(AbstractEventRecorder.class);
	
	private InputState state;

	protected IEventRecorder eventRecorder;

	AbstractEventRecorder(
			final InputState state, 
			final IEventRecorder eventRecorder
	) {
		this.state = state;
		this.eventRecorder = eventRecorder;
	}

	@Override
	public void eventDispatched(final AWTEvent event) {
		processEvent(event);
	}

	protected String getEventComponentLocator(AWTEvent aEvent) {
		final Component component = getEventComponent(aEvent);
		return eventRecorder.getComponentLocator(component);
	}

	private Component getEventComponent(final AWTEvent aEvent) {
		Component component = aEvent instanceof ComponentEvent ? ((ComponentEvent) aEvent).getComponent() : null;
		if (component == null) {
			component = state.deepestComponentUnderMousePointer();
		}
		return component;
	}

	protected static String getEventValue(final AWTEvent aEvent) {
		if (aEvent instanceof KeyEvent) {
			return getKeyEvent(aEvent);
		}
		else if (aEvent instanceof MouseEvent) {
			return getMouseEvent(aEvent);
		}
		else if (aEvent instanceof FocusEvent) {
			return getFocusEvent(aEvent);
		} 
		return null;
	}

	private static String getFocusEvent(final AWTEvent aEvent) {
		final FocusEvent fEvent = (FocusEvent) aEvent;
		if (fEvent.getComponent() instanceof JCheckBox) {
			return getCheckBoxFocusEvent(fEvent);
		}
		if (fEvent.getComponent() instanceof JTextField) {
			return getTextFieldFocusEvent(fEvent);
		}
		if (fEvent.getComponent() instanceof JTextComponent) {
			return getTextComponentFocusEvent(fEvent);
		}
		if (fEvent.getComponent() instanceof JComboBox) {
			return getComboBoxFocusEvent(fEvent);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String getComboBoxFocusEvent(final FocusEvent fEvent) {
		final Object selectedItem = ((JComboBox) fEvent.getComponent()).getSelectedItem();
		final JList list = new JList(((JComboBox) fEvent.getComponent()).getModel());
		final Component listCellRendererComponent = ((JComboBox) fEvent.getComponent()).getRenderer().getListCellRendererComponent(list, selectedItem, 0, false, false);
		if (selectedItem != null) {
			if (listCellRendererComponent instanceof JTextField) {
				return ((JTextField) listCellRendererComponent).getText();
			} 
			else if (listCellRendererComponent instanceof JLabel) {
				return ((JLabel) listCellRendererComponent).getText();
			} 
			else if (selectedItem instanceof String) {
				return selectedItem.toString();
			} 
			else {
				return "unknowObjectType";
			}
		}
		return null;
	}

	private static String getTextComponentFocusEvent(final FocusEvent fEvent) {
		return ((JTextComponent) fEvent.getComponent()).getText();
	}

	private static String getTextFieldFocusEvent(final FocusEvent fEvent) {
		return ((JTextField) fEvent.getComponent()).getText();
	}

	private static String getCheckBoxFocusEvent(final FocusEvent fEvent) {
		return Boolean.toString(((JCheckBox) fEvent.getComponent()).isSelected());
	}

	private static String getMouseEvent(final AWTEvent aEvent) {
		final MouseEvent mEvent = (MouseEvent) aEvent;
		final Component componentEvent = mEvent.getComponent();
		if (componentEvent instanceof JTextField) {
			return getTextFieldMouseEvent(mEvent);
		} 
		else if (componentEvent instanceof JTable) {
			return getJTableMouseEvent(mEvent);
		}
		else if (componentEvent instanceof JList) {
			return getJListMouseEvent(mEvent);
		}
		return null;
	}

	private static String getTextFieldMouseEvent(final MouseEvent mEvent) {
		return ((JTextField) mEvent.getComponent()).getText();
	}

	private static String getJTableMouseEvent(final MouseEvent mEvent) {
		final JTable jSyTable = (JTable) mEvent.getComponent();
		final int selectedRowIndex = jSyTable.getSelectedRow();
		final int[] selectedColumns = jSyTable.getSelectedColumns();
		final int length = selectedColumns.length;
		final List<String> criteria = new ArrayList<String>(length);
		if(length > 0) {
			for(int columnIndex: selectedColumns) {
				final TableModel jTableModel = jSyTable.getModel();
				final String columnName = jTableModel.getColumnName(columnIndex);
				final Object cellValue = jTableModel.getValueAt(selectedRowIndex, columnIndex);
				criteria.add(columnName + Property.TABLE_KEY_VALUE_SEPARATOR + cellValue);
			}
			return StringUtils.join(criteria, Property.TABLE_CRITERIA_SEPARATOR);
		}
		return "No Cell Selected";
	}

	private static String getJListMouseEvent(final MouseEvent event) {
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
	
	private static String getKeyEvent(final AWTEvent aEvent) {
		final KeyEvent event = (KeyEvent) aEvent;
		if (event.getID() == KeyEvent.KEY_RELEASED) {
			return Character.toString(event.getKeyChar());
		}
		return null;
	}

	protected String getEventComponentLabel(AWTEvent aEvent) {
		Component component = getEventComponent(aEvent);
		return getComponentName(component);
	}

	protected static String getComponentName(Component component) {
		if (component instanceof AbstractButton) {
			final AbstractButton b = (AbstractButton) component;
			return b.getText();
		} 
		return component != null ? component.getName() : null;
	}
	
	protected static String getEventComponentContainer(final AWTEvent event) {
		Component component = (Component) event.getSource();
		Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JDialog.class, component);
		String ancestorLocator = null;
		if(ancestorOfClass != null) {
			ancestorLocator = ((JDialog)ancestorOfClass).getTitle();
		}
		if(ancestorOfClass == null) {
			ancestorOfClass = SwingUtilities.getAncestorOfClass(JLayeredPane.class, component);
			if(ancestorOfClass != null){
				ancestorLocator = ancestorOfClass.getClass().getSimpleName();
			}
		}
		if (ancestorOfClass == null) {
			ancestorOfClass = SwingUtilities.getAncestorOfClass(JTabbedPane.class, component);
			if((ancestorOfClass instanceof JTabbedPane)){
				ancestorLocator = ((JTabbedPane)ancestorOfClass).getTitleAt(((JTabbedPane)ancestorOfClass).getSelectedIndex());
			}
		}
		if (ancestorOfClass == null) {
			ancestorOfClass = SwingUtilities.getAncestorOfClass(JFrame.class, component);
			if((ancestorOfClass instanceof JFrame)){
				ancestorLocator = ((JFrame)ancestorOfClass).getTitle();
			}
		}
		return ancestorLocator;
	}
	
	protected void appendEventRecord(final AWTEventCapturedObject captureEvent) {
		LOG.info("New record event captured: {}", ToStringBuilder.reflectionToString(captureEvent, ToStringStyle.SIMPLE_STYLE));
		eventRecorder.appendInfo(captureEvent);
	}
	
	protected static boolean isCapturedEventUninteresting(final AWTEventCapturedObject captureEvent) {
		return captureEvent.businessValue == null && captureEvent.componentLocator == null;
	}

}