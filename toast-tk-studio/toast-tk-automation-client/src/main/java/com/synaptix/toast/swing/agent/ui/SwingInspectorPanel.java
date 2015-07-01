
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.net.response.ScanResponse;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.constant.Resource;

public class SwingInspectorPanel extends JPanel {
	private static final long serialVersionUID = 4749771836758704761L;
	private DefaultListModel listModel;
	
	private final JList list;
	
	private final JTextField pageName = new JTextField(15);
	private final JTextField itemFilter = new JTextField(15);
	
	private final JButton search;
	private final JButton scanButton;
	private final JButton saveButton;
	private final JButton clear;
	private final Config config;
	
	private List<String> oldList = new ArrayList<String>();
	private final ISwingAutomationClient cmdServer;
	private final JPanel toolPanel = new JPanel();
    
    @Inject
	public SwingInspectorPanel(ISwingAutomationClient cmdServer, EventBus evenBus, Config config) {
    	this.cmdServer = cmdServer;
    	
    	this.config = config;
    	
    	this.search = new JButton("Filter", new ImageIcon(Resource.ICON_FILTER_16PX_IMG));
    	this.search.setToolTipText("Filter the items not containig the filter term..");
    	
    	this.scanButton = new JButton("Scan", new ImageIcon(Resource.ICON_SCAN_16PX_IMG)); 
    	this.scanButton.setToolTipText("List the widgets currently displayed on the SUT user interface..");
    	
    	this.saveButton = new JButton("Save...", new ImageIcon(Resource.ICON_SAVE_16PX_IMG));
    	this.saveButton.setToolTipText("Save selected items within the object repository as a new page..");
    	
    	this.clear = new JButton("Clear", new ImageIcon(Resource.ICON_CLEAR_16PX_IMG));
    	this.clear.setToolTipText("Clear result list..");
    	   
    	evenBus.register(this);
		toolPanel.setAlignmentX(RIGHT_ALIGNMENT);
		toolPanel.add(new JLabel("Page Name:"));
		toolPanel.add(pageName);
		toolPanel.add(clear);
		toolPanel.add(new JSeparator());
		toolPanel.add(scanButton);
		toolPanel.add(new JSeparator());
		toolPanel.add(saveButton);
		toolPanel.add(itemFilter);
		toolPanel.add(search);
		toolPanel.setAlignmentX(LEFT_ALIGNMENT);

		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 500));
		setLayout(new BorderLayout());
		add(toolPanel, BorderLayout.PAGE_START);
		add(listScroller, BorderLayout.CENTER);
		
		initActions();
	}

	private void initActions() {
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValues() != null && list.getSelectedIndices().length == 1) {
					cmdServer.highlight((String) list.getSelectedValue());
				}
			}
		});
		 search.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = itemFilter.getText();
					listModel.clear();
					if(text.isEmpty()){
						for(int i =0; i<oldList.size(); i++){
					        listModel.addElement(oldList.get(i));
						}
					}else{
						Set<String> newList = new HashSet<String>();
						for(int i = 0; i < oldList.size(); i++){
							String item = (String)oldList.get(i);
							if(item.contains(text)){
						        listModel.addElement(item);
							}
						}
						addInspectComponents(newList);
					}				
				}
			});

	        clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					flush();
				}
			});
	        
	        saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveButton.setEnabled(false);
					RestUtils.postPage(config.getWebAppAddr(), config.getWebAppPort(), pageName.getText(), list.getSelectedValues());
					saveButton.setEnabled(true);
				}
			});
	        
	        scanButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cmdServer.scanUi(true);
				}
			});
	}

    public void addInspectComponents(final Set<String> s) {
    	for(final String s_: s){
    		addInspectComponent(s_);
    	}
    }

    public void addInspectComponent(final String s) {
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				oldList.add(s);
		        listModel.addElement(s);
			}
		});
    }
    
    @Subscribe
    public void handleScanResponseEvent(final ScanResponse event){
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				flush();
				addInspectComponents(event.getComponents());
			}
		});
    }
    

    public void flush(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pageName.setText("");
				listModel.clear();
				oldList.clear();
			}
		});
    } 
}
