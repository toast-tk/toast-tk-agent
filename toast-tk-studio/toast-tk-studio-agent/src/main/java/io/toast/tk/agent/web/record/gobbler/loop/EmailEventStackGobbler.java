package io.toast.tk.agent.web.record.gobbler.loop;

import java.util.Arrays;
import java.util.List;

public class EmailEventStackGobbler extends InputEventStackGobbler {

	@Override
	public List<String> getSupportedComponents() {
		return Arrays.asList("email");
	}


}
