package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.fit.cssbox.swingbox.BrowserPane;

import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.runtime.core.IReportUpdateCallBack;
import com.synaptix.toast.swing.agent.AgentBoot;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;
import com.synaptix.toast.swing.agent.runtime.StudioScriptRunner;

public class RunScriptActionListener implements ActionListener {

	private StudioScriptRunner runner;

	private ISwingAutomationClient recorder;

	private JTextArea interpretedOutputArea;

	private final MongoRepositoryCacheWrapper mongoRepoManager;

	public RunScriptActionListener(
		ISwingAutomationClient recorder,
		JTextArea interpretedOutputArea,
		StudioScriptRunner runner,
		MongoRepositoryCacheWrapper mongoRepoManager) {
		this.runner = runner;
		this.recorder = recorder;
		this.interpretedOutputArea = interpretedOutputArea;
		this.mongoRepoManager = mongoRepoManager;
	}

	@Override
	public void actionPerformed(
		ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final String test = interpretedOutputArea.getText();
				if(!recorder.isConnected()) {
					JOptionPane.showMessageDialog(
						null,
						"Automation agent offline, please launch the System Under Test with an active agent!");
				}
				else if(test != null && !test.isEmpty()) {
					if(runner == null) {
						runner = new StudioScriptRunner(AgentBoot.injector);
					}
					final String wikiScenario = toWikiScenario(test);
					final BrowserPane swingbox = new BrowserPane();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JDialog dialog = new JDialog();
							dialog.setSize(500, 300);
							dialog.setTitle("Execution report..");
							dialog.setLayout(new BorderLayout());
							dialog.setModalityType(ModalityType.APPLICATION_MODAL);
							dialog.add(swingbox);
							dialog.setVisible(true);
						}
					});
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							try {
								runner.runLocalScript(
									wikiScenario,
									mongoRepoManager.getWikiFiedRepo(),
									new IReportUpdateCallBack() {

										@Override
										public void onUpdate(
											final String report) {
											swingbox.setText(report);
											swingbox.revalidate();
										}

										@Override
										public void onFatalStepError(
											String message) {
											JOptionPane.showMessageDialog(null, message);
										}
									});
							}
							catch(IllegalAccessException e) {
								e.printStackTrace();
							}
							catch(ClassNotFoundException e) {
								e.printStackTrace();
							}
							catch(IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
				else {
					JOptionPane.showMessageDialog(null, "Script Text Area is Empty !");
				}
			}
		});	
	}

	private String toWikiScenario(
		final String test) {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append("|| scenario || swing ||\n"); // TODO: bind type to selected
// descriptor
		final String[] lines = test.split("\n");
		for(final String line : lines) {
			sb.append('|').append(line).append('|').append('\n');
		}
		return sb.toString();
	}
}
