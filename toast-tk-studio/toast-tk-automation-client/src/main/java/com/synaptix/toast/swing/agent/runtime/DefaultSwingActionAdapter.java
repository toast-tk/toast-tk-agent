package com.synaptix.toast.swing.agent.runtime;

import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.AbstractSwingActionAdapter;
import com.synaptix.toast.automation.driver.swing.RemoteSwingAgentDriverImpl;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.runtime.IRepositorySetup;

@ActionAdapter(value = ActionAdapterKind.swing, name = "ToastSwingClientAdapter")
public class DefaultSwingActionAdapter extends AbstractSwingActionAdapter {

	@Inject
	public DefaultSwingActionAdapter(
		IRepositorySetup repo,
		RemoteSwingAgentDriverImpl driver) {
		super(repo, driver);
	}
}
