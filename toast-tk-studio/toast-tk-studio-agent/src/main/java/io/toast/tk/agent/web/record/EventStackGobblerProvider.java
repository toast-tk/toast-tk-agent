package io.toast.tk.agent.web.record;

import java.util.ArrayList;
import java.util.List;

import io.toast.tk.agent.web.record.gobbler.loop.DateEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.loop.EmailEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.loop.NumberEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.loop.PasswordEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.loop.SearchEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.loop.TextEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.simple.ButtonClickEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.simple.LinkEventStackGobbler;
import io.toast.tk.agent.web.record.gobbler.simple.SelectEventStackGobbler;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class EventStackGobblerProvider {

	static List<EventStackGobbler> gobblers = new ArrayList<EventStackGobbler>();
	
	static {
		gobblers.add(new TextEventStackGobbler());
		gobblers.add(new PasswordEventStackGobbler());
		gobblers.add(new NumberEventStackGobbler());
		gobblers.add(new EmailEventStackGobbler());
		gobblers.add(new DateEventStackGobbler());
		gobblers.add(new SearchEventStackGobbler());
		gobblers.add(new ButtonClickEventStackGobbler());
		gobblers.add(new LinkEventStackGobbler());
		gobblers.add(new SelectEventStackGobbler());
	}

	public static final EventStackGobbler get(
			WebEventRecord event) {
		for(EventStackGobbler gobbler : gobblers) {
			if(gobbler.isInterestedIn(event)) {
				return gobbler;
			}
		}
		return null;
	}
}
