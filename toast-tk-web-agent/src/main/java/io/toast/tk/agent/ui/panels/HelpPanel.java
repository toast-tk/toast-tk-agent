package io.toast.tk.agent.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class HelpPanel extends AbstractFrame {
	
	private static final Logger LOG = LogManager.getLogger(HelpPanel.class);

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
		
		textField = new JTextArea(getStringFromInputStream(inputStream));
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
				if(askUser()) {
					send();
				}
			}
		});
		powerButton.setAlignmentX(Component.LEFT_ALIGNMENT);	
		powerButton.setAlignmentY(Component.CENTER_ALIGNMENT);
	    panel.add(powerButton);
	    return panel;
	}
	
	public boolean askUser() {
		String question = "Are you sure to send this email to the toast support team ?";	
		this.setVisible(false);
		int option = JOptionPane.showConfirmDialog(null, question, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(option == JOptionPane.YES_OPTION){
		     return true;
		} else {
			this.setVisible(true);
			return false;
		}
	}
	
	private void send() {
		String text = textField.getText();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String date = df.format(new Date());
		String subject = "[TOAST] Issue on " + date;
		List<String> files = getFileToSend();
		
		List<String> mailTo = new ArrayList<String>();
		mailTo.add(Property.TOAST_CONTACT);

		this.dispose();
		MailSender sender = new MailSender();
		try {
			sender.send(subject, text, mailTo, provider.get().getMailTo(), files);
			NotificationManager.showMessage(CommonMessages.HELP_MESSAGE_SENT).showNotification();
		}
		catch(Exception e) {
			LOG.error(e.getMessage());
			NotificationManager.showMessage(CommonMessages.HELP_MESSAGE_ERROR + Property.TOAST_CONTACT).showNotification();
		}

	}
	
	private List<String> getFileToSend() {
		List<String> res = new ArrayList<String>();
		String logRep = Property.TOAST_LOG_DIR + "log4j.log";
		res.add(logRep);
		String logRep2 = Property.TOAST_LOG_DIR + "toast-log.log";
		res.add(logRep2);
		String resultRep = FileHelper.getLastModifiedFile(Property.TOAST_TARGET_DIR);
		if(resultRep != "") {
			res.add(resultRep);
		}
		return res;
	}
		
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	@Inject
	public HelpPanel(AgentConfigProvider provider) throws IOException {
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
