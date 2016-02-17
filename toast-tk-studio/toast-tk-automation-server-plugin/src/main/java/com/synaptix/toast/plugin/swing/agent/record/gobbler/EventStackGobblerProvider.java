package com.synaptix.toast.plugin.swing.agent.record.gobbler;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.looper.ComboBoxClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.looper.InputEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.ButtonClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.CheckBoxClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.MenuClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.MenuItemClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.PanelFocusEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.PopupClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.RadioButtonClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.TableClickEventStackGobbler;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.simple.WindowFocusEventStackGobbler;

public class EventStackGobblerProvider {

	static List<EventStackGobbler> gobblers = new ArrayList<EventStackGobbler>();
	static {
		gobblers.add(new ComboBoxClickEventStackGobbler());
		gobblers.add(new InputEventStackGobbler());
		gobblers.add(new MenuClickEventStackGobbler());
		gobblers.add(new MenuItemClickEventStackGobbler());
		gobblers.add(new ButtonClickEventStackGobbler());
		gobblers.add(new RadioButtonClickEventStackGobbler());
		gobblers.add(new CheckBoxClickEventStackGobbler());
		gobblers.add(new TableClickEventStackGobbler());
		gobblers.add(new PopupClickEventStackGobbler());
		gobblers.add(new WindowFocusEventStackGobbler());
		gobblers.add(new PanelFocusEventStackGobbler());
	}

	public static final EventStackGobbler get(
		AWTCapturedEvent awtCapturedEvent) {
		EventStackGobbler firstGobblerInterestedInEvent = null;
		for(EventStackGobbler gobbler : gobblers) {
			if(gobbler.isInterestedIn(awtCapturedEvent)) {
				firstGobblerInterestedInEvent = gobbler;
			}
		}
		return firstGobblerInterestedInEvent;
	}
}
