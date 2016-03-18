package com.synaptix.toast.agent.web.record;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.toast.agent.web.record.gobbler.ButtonClickEventStackGobbler;
import com.synaptix.toast.agent.web.record.gobbler.InputEventStackGobbler;
import com.synaptix.toast.agent.web.record.gobbler.LinkEventStackGobbler;
import com.synaptix.toast.agent.web.record.gobbler.SelectEventStackGobbler;
import com.synaptix.toast.agent.web.record.gobbler.loop.TextEventStackGobbler;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class EventStackGobblerProvider {

	static List<EventStackGobbler> gobblers = new ArrayList<EventStackGobbler>();
	static {
		gobblers.add(new TextEventStackGobbler());
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
