package com.synaptix.toast.swing.agent.ui;

import javax.swing.JPanel;

import com.google.inject.Inject;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;

public class CorpusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	@Inject
	public CorpusPanel(
			final HomePanel homePanel,
			final SwingInspectionRecorderPanel recorderPanel,
			final SwingInspectorPanel inspectorPanel,
			final AdvancedSettingsPanel advancedSettingsPanel) {
		super();
	}
}
