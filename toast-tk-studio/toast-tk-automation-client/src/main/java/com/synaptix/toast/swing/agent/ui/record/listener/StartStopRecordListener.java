package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.swing.agent.constant.Resource;


public class StartStopRecordListener implements ActionListener{

	private static final Logger LOG = LogManager.getLogger(StartStopRecordListener.class);
	
	private ISwingAutomationClient recorder;

	private boolean recordingActive;
	
	private static final String startRecordingLabel = "Start";

	private static final String stopRecordingLabel = "Stop";
	
	private static final ImageIcon startRecordingIcon = new ImageIcon(Resource.ICON_RUN_16PX_IMG);

	private static final ImageIcon stopRecordingIcon = new ImageIcon(Resource.ICON_STOP_16PX_IMG);
	
	private final JButton startStopRecordButton;

	public StartStopRecordListener(ISwingAutomationClient recorder, JButton startStopRecordButton){
		this.recorder = recorder;
		this.startStopRecordButton = startStopRecordButton;
	}
	
	@Override
	public void actionPerformed(
		ActionEvent e) {
		try {
			if(recorder.isConnected()) {
				if(recorder.isWebMode()){
					String url = JOptionPane.showInputDialog("URL: ");
					if(!StringUtils.isEmpty(url)){
						if(!recordingActive) {
							startWebRecording(url);
						}
						else {
							stopRecording();
						}
					}
				}else{
					if(!recordingActive) {
						startRecording();
					}
					else {
						stopRecording();
					}
				}
			}
			else {
				startStopRecordButton.setText(startRecordingLabel);
				startStopRecordButton.setIcon(startRecordingIcon);
				recordingActive = false;
			}
		}
		catch(Exception e1) {
			LOG.error(e1.getMessage(), e1);
		}		
	}

	private void startWebRecording(
		String url) {
		recordingActive = true;
		startStopRecordButton.setText(stopRecordingLabel);
		startStopRecordButton.setIcon(stopRecordingIcon);
		recorder.startRecording(url);
	}

	private void startRecording() {
		recordingActive = true;
		startStopRecordButton.setText(stopRecordingLabel);
		startStopRecordButton.setIcon(stopRecordingIcon);
		recorder.startRecording();
	}

	public void stopRecording() {
		recordingActive = false;
		startStopRecordButton.setText(startRecordingLabel);
		startStopRecordButton.setIcon(startRecordingIcon);
		recorder.stopRecording();
	}
}
