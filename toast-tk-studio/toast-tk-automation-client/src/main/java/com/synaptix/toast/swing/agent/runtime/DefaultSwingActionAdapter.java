package com.synaptix.toast.swing.agent.runtime;

import com.google.inject.Inject;

import io.toast.tk.adapter.swing.AbstractSwingActionAdapter;
import io.toast.tk.automation.driver.swing.RemoteSwingAgentDriverImpl;
import io.toast.tk.core.adapter.ActionAdapterKind;
import io.toast.tk.core.annotation.ActionAdapter;
import io.toast.tk.runtime.IActionItemRepository;

@ActionAdapter(value = ActionAdapterKind.swing, name = "ToastSwingClientAdapter")
public class DefaultSwingActionAdapter extends AbstractSwingActionAdapter {

	@Inject
	public DefaultSwingActionAdapter(
		IActionItemRepository repo,
		RemoteSwingAgentDriverImpl driver) {
		super(repo, driver);
	}
}
