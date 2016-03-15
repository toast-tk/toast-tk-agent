package com.synaptix.toast.swing.agent.web.record;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.web.record.component.ButtonClickEventStackGobbler;
import com.synaptix.toast.swing.agent.web.record.component.InputEventStackGobbler;
import com.synaptix.toast.swing.agent.web.record.component.LinkEventStackGobbler;
import com.synaptix.toast.swing.agent.web.record.component.SelectEventStackGobbler;

public class EventStackGobblerProvider {

	static List<EventStackGobbler> gobblers = new ArrayList<EventStackGobbler>();
	static {
		gobblers.add(new InputEventStackGobbler());
		gobblers.add(new ButtonClickEventStackGobbler());
		gobblers.add(new LinkEventStackGobbler());
		gobblers.add(new SelectEventStackGobbler());
	}

	public static final EventStackGobbler get(
			WebEventRecord event) {
		EventStackGobbler firstGobblerInterestedInEvent = null;
		for(EventStackGobbler gobbler : gobblers) {
			if(gobbler.isInterestedIn(event)) {
				firstGobblerInterestedInEvent = gobbler;
			}
		}
		return firstGobblerInterestedInEvent;
	}
}
