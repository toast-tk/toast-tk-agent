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
package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.IStudioApplication;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.swing.agent.AgentBoot;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.runtime.StudioScriptRunner;
import com.synaptix.toast.swing.agent.runtime.SutRunnerAsExec;

@Deprecated
public class SwingAgentScriptRunnerPanel extends JPanel {

	private static final long serialVersionUID = 4749771836758704761L;

	private static final Logger LOG = LogManager.getLogger(SwingAgentScriptRunnerPanel.class);

	// private final JButton attachButton;
	private final JButton runButton;

	private final JButton initButton;

	private final JButton runtimePropertyButton;

	private final JPanel toolPanel;

	private final JTextArea script;

	private final Properties properties;

	private final File toastPropertiesFile;

	private StudioScriptRunner runner;

	private final SutRunnerAsExec runtime;

	private final IStudioApplication app;

	@Inject
	public SwingAgentScriptRunnerPanel(
		final SutRunnerAsExec runtime,
		final IStudioApplication app,
		@StudioEventBus EventBus eventBus) {
		eventBus.register(this);
		this.runtime = runtime;
		this.app = app;
		// this.attachButton = new JButton("Attach");
		this.toolPanel = new JPanel();
		this.runButton = new JButton("Run Test", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.runButton.setToolTipText("Execute current scenario..");
		this.runButton.setEnabled(false);
		this.runtimePropertyButton = new JButton("Settings", new ImageIcon(Resource.ICON_CONF_16PX_2_IMG));
		this.runtimePropertyButton.setToolTipText("Edit runtime properties..");
		this.initButton = new JButton("Start SUT", new ImageIcon(Resource.ICON_POWER_16PX_IMG));
		this.initButton.setToolTipText("Start the system under test..");
		if(app.isConnected()) {
			disableInitButton();
		}
		else {
			enableInitButton();
		}
		this.toastPropertiesFile = new File(Config.TOAST_PROPERTIES_FILE);
		// this.attachButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// final List<VirtualMachineDescriptor> vms = VirtualMachine.list();
		// String[] jvms = new String[vms.size()];
		// for (int i = 0; i < vms.size(); i++) {
		// jvms[i] = vms.get(i).displayName();
		// }
		//
		// final ListPanel elp = new ListPanel(jvms);
		// JOptionPane.showMessageDialog(null, elp);
		// int selectedIndex = elp.getSelectedIndex();
		// if(selectedIndex > -1){
		// LOG.info("EditableListPanel value: " + selectedIndex);
		//
		// // attach to target VM
		// VirtualMachine vm = null;
		// try {
		// vm = VirtualMachine.attach(vms.get(selectedIndex).id());
		// // get system properties in target VM
		// Properties props = vm.getSystemProperties();
		//
		// // construct path to management agent
		// props.put("redpepper.plugin.dir", Property.TOAST_PLUGIN_DIR);
		// initProperties(toastPropertiesFile);
		// String agentPath = (String)
		// properties.get(Property.TOAST_RUNTIME_AGENT);
		//
		// // load agent into target VM
		// vm.loadAgent(agentPath, "com.sun.management.jmxremote.port=5000");
		//
		// } catch (AttachNotSupportedException e1) {
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// } catch (AgentLoadException e1) {
		// e1.printStackTrace();
		// } catch (AgentInitializationException e1) {
		// e1.printStackTrace();
		// }finally{
		// try {
		// if(vm != null){
		// vm.detach();
		// }
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// }
		// }
		// }
		// });
		//
		// this.toolPanel.add(attachButton);
		this.toolPanel.add(initButton);
		this.toolPanel.add(runButton);
		this.toolPanel.add(runtimePropertyButton);
		this.toolPanel.setAlignmentX(CENTER_ALIGNMENT);
		this.properties = new Properties();
		this.script = new JTextArea();
		this.script.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(
				DocumentEvent e) {
				checkEmpty();
			}

			@Override
			public void insertUpdate(
				DocumentEvent e) {
				checkEmpty();
			}

			@Override
			public void changedUpdate(
				DocumentEvent e) {
				checkEmpty();
			}

			private void checkEmpty() {
				runButton.setEnabled(!script.getText().isEmpty());
			}
		});
		final JScrollPane listScroller = new JScrollPane(script);
		listScroller.setPreferredSize(new Dimension(250, 500));
		this.setLayout(new BorderLayout());
		this.add(toolPanel, BorderLayout.PAGE_START);
		this.add(listScroller, BorderLayout.CENTER);
		this.initActions();
	}

	private void enableInitButton() {
		this.initButton.setBackground(Color.GREEN);
		this.initButton.setEnabled(true);
	}

	private void disableInitButton() {
		this.initButton.setBackground(Color.RED);
		this.initButton.setEnabled(false);
	}

	private void initProperties(
		File toastProperties) {
		try {
			properties.load(FileUtils.openInputStream(toastProperties));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void initActions() {
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(
				ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						String test = script.getText();
						if(test != null) {
							// TODO: check if client is connected !
							if(runner == null) {
								runner = new StudioScriptRunner(AgentBoot.injector);
							}
							String wikiScenario = toWikiScenario(test);
							try {
								runner.runRemoteScript(wikiScenario);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							JOptionPane.showMessageDialog(null, "Script Text Area is Empty !");
						}
					}
				});
			}

			private String toWikiScenario(
				String test) {
				String output = "|| scenario || swing ||\n";
				String[] lines = test.split("\n");
				for(String line : lines) {
					output += "|" + line + "|\n";
				}
				return output;
			}
		});
		this.initButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(
				ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground()
						throws Exception {
						disableInitButton();
						initProperties(toastPropertiesFile);
						String runtimeType = (String) properties.get(Property.TOAST_RUNTIME_TYPE);
						try {
							publish();
							runtime.init(runtimeType, true);
							Desktop.getDesktop().open(new File(Config.TOAST_HOME_DIR));
						}
						catch(IllegalAccessException e) {
							e.printStackTrace();
						}
						catch(SAXException e) {
							e.printStackTrace();
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						catch(ParserConfigurationException e) {
							e.printStackTrace();
						}
						finally {
							app.stopProgress("Done, now run the created bat file !");
						}
						return null;
					}

					@Override
					protected void process(
						List<Void> chunks) {
						super.process(chunks);
						app.startProgress("Starting SUT..");
					}
				};
				worker.execute();
			}
		});
		this.runtimePropertyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(
				ActionEvent e) {
				initProperties(toastPropertiesFile);
				final ConfigPanel configPanel = new ConfigPanel(properties, toastPropertiesFile);
			}
		});
	}

	@Subscribe
	public void handleServerConnexionStatus(
		SeverStatusMessage startUpMessage) {
		switch(startUpMessage.state) {
			case CONNECTED :
				disableInitButton();
				break;
			default :
				enableInitButton();
				break;
		}
	}
}
