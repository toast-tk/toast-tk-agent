package io.toast.tk.agent.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.NotificationManager;
import io.toast.tk.agent.ui.i18n.CommonMessages;
import io.toast.tk.agent.ui.utils.PanelHelper;
import io.toast.tk.runtime.constant.Property;
import io.toast.tk.runtime.mail.MailSender;
import io.toast.tk.runtime.parse.FileHelper;

public class MailHelpPanel extends AbstractFrame {
	
	private static final Logger LOG = LogManager.getLogger(MailHelpPanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 313204979777442842L;

	private AgentConfigProvider provider;
	
	private JTextArea textField;
	
    private final int textHeight = 400;
    private final int textWidth = 600;

    public void buildPanel() throws IOException {
    	
		Image toastLogo = PanelHelper.createImage(this,"ToastLogo.png");
		this.setIconImage(toastLogo);
		
        this.setContentPane(buildMainPanel());
        
        this.setTitle("Send an issue");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(textWidth + 25, textHeight + 150);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.lowerFrame();
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }
    
    private JPanel buildMainPanel() throws IOException {
    	JPanel dp = PanelHelper.createBasicJPanel();
    	
    	JPanel textFieldPanel = createBasicTextPanel();  
		dp.add(textFieldPanel);

		JPanel buttonPanel = buildRightPanelButton();
		dp.add(buttonPanel);
		
		dp.setLayout(new BoxLayout(dp, BoxLayout.PAGE_AXIS));
		
		return dp;
    }

	private JPanel createBasicTextPanel() {
		JPanel panel = PanelHelper.createBasicJPanel("Issue to send", PanelHelper.FONT_TITLE_1);
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("basicMailText.txt");
		
		textField = new JTextArea(FileHelper.getStringFromInputStream(inputStream));
		textField.setColumns(30);
		textField.setLineWrap(true);
		
		JScrollPane scrolltxt = new JScrollPane(textField);
		Dimension prefDim = new Dimension(textWidth, textHeight);
		Dimension moyDim = new Dimension(textWidth + 50, textHeight + 50);
		scrolltxt.setPreferredSize(prefDim);
		scrolltxt.setMaximumSize(moyDim);
		scrolltxt.setFont(PanelHelper.FONT_TEXT);
		scrolltxt.setAlignmentX(Component.LEFT_ALIGNMENT);	
		scrolltxt.setAlignmentY(Component.CENTER_ALIGNMENT);
		scrolltxt.setBackground(Color.white);
		
		panel.add(scrolltxt);
		return panel;
	}

	private JPanel buildRightPanelButton() throws IOException {
		JPanel panel = PanelHelper.createBasicJPanel();
		Image powerButtonImg = PanelHelper.createImage(this,"send_button.png");
		JButton powerButton = buildIconButton("Send mail", powerButtonImg);
		powerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(askUser("Confirm send", CommonMessages.MAIL_SEND_CONFIRM, JOptionPane.QUESTION_MESSAGE)) {
					send();
				}
			}
		});
		powerButton.setAlignmentX(Component.LEFT_ALIGNMENT);	
		powerButton.setAlignmentY(Component.CENTER_ALIGNMENT);
	    panel.add(powerButton);
	    return panel;
	}
	
	public boolean askUser(String title, String question, int typeMessage) {
		this.setVisible(false);
		int option = JOptionPane.showConfirmDialog(null, question, title, JOptionPane.YES_NO_OPTION, typeMessage);
		
		if(option == JOptionPane.YES_OPTION){
		     return true;
		} else {
			this.setVisible(true);
			return false;
		}
	}
	
	private void send() {
		this.dispose();

		Thread thread;
		thread = new Thread(new SenderThread());  
		thread.start();
	}
	
	protected class SenderThread implements Runnable {

		  public void run() {	
			  String text = textField.getText();
			  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			  String date = df.format(new Date());
			  String subject = "[TOAST] Issue on " + date;
			  List<String> files = getFileToSend();
			  
			  List<String> mailTo = new ArrayList<String>();
			  mailTo.add(Property.TOAST_CONTACT);
			  
			  MailSender sender = new MailSender();
			  
			  if(provider.get().getProxyActivate().equals("true")) {
				  sender.setProxy(provider.get().getProxyAdress(), provider.get().getProxyPort());
			  }
			  
			  try {
			  	sender.send(subject, text, mailTo, provider.get().getSmtpUser(), files);
			  	NotificationManager.showMessage(CommonMessages.HELP_MESSAGE_SENT).showNotification();
			  }
			  catch(Exception e) {
			  	LOG.error(e);
			  	NotificationManager.showMessage(CommonMessages.HELP_MESSAGE_ERROR + Property.TOAST_CONTACT).showNotification();
			  	
			  	if(askUser("", CommonMessages.MOVEMENT_LOG_FILES, JOptionPane.WARNING_MESSAGE)) {
				  	moveFileToDownload(files, text);
				  	NotificationManager.showMessage(CommonMessages.USEFULL_FILE_MOVED).showNotification();
				}
			  }
		  }
	}
	
	private List<String> getFileToSend() {
		List<String> res = new ArrayList<String>();
		String logRep = Property.TOAST_LOG_DIR + "log4j.log";
		res.add(logRep);
		String logRep2 = Property.TOAST_HOME_DIR + "toast-logs.log";
		res.add(logRep2);
		String resultRep = FileHelper.getLastModifiedFile(Property.TOAST_TARGET_DIR);
		if(resultRep != "") {
			res.add(resultRep);
		}
		return res;
	}
	
	private void moveFileToDownload(List<String> files, String body) {
		String dir = Property.DOWNLOAD_DIR + "to_send_to_" + Property.TOAST_CONTACT + File.separatorChar;
		FileHelper.createDirectory(dir);
		
		for(String file : files) {
			try {
				File fileTemp = new File(file);
				File fileToDelete = new File(dir + fileTemp.getName());
				if(fileToDelete.exists()) {
					fileToDelete.delete();
				}
				FileHelper.copyFile(file, dir);
			} catch (IOException e) {
				LOG.error(e);
			}
		}

		try {
			FileHelper.writeFile(body, dir + "Mail_Body.txt");
		} catch (IOException e) {
			LOG.error(e);
		}
	} 

	@Inject
	public MailHelpPanel(AgentConfigProvider provider) throws IOException {
    	this.provider = provider;
		buildPanel();
    }

    private void lowerFrame() {

            Dimension windowSize = this.getSize();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Point centerPoint = ge.getCenterPoint();

            int dx = centerPoint.x - windowSize.width / 2;
            int dy = centerPoint.y - windowSize.height / 2;    
            this.setLocation(dx, dy);
    }
}
