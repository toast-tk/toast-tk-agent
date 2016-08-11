package io.toast.tk.agent.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




/**
 * Stub for displaying Configuration item.
 */
public class ConfigPanel extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private String chromeDriverName = "chromedriver";
	private String webAppName = "webapp";
	private String recorderName = "recording";

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);

	private JPanel mainPane;

	private JLabel labelChrome, labelWebApp, labelRecorder;
	private JTextField textFieldChrome, textFieldWebApp, textFieldRecorder;
	private JPanel textButtonPanelChrome, textButtonPanelWebApp, textButtonPanelRecorder,
		iconPanelChrome, iconPanelWebApp, iconPanelRecorder;
	private JLabel errorLabelChrome, errorLabelWebApp, errorLabelRecorder;
	private JButton fileSearchChrome;

	private JLabel iconValidChrome, iconValidWebApp, iconValidRecorder, 
		iconNotValidChrome, 	iconNotValidWebApp, 	iconNotValidRecorder;

	private final Properties properties;

	private HashMap<String, JTextField> textFields;

	private final File propertyFile;
	
	private Image notvalid_image;
	private Image valid_image;
	
	private String errorMessageSelectFile = "The file that you have selected do not exist.";
	private String errorMessageSelectURL = "The URL does not anwser.";
	
	/**
	 * This is the default constructor
	 * 
	 * @param propertiesConfiguration
	 * @throws IOException 
	 */
	public ConfigPanel(
		Properties propertiesConfiguration,
		File propertyFile) throws IOException {
		super();
		this.properties = propertiesConfiguration;
		this.propertyFile = propertyFile;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mainPane = new JPanel();
		
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		this.textFields = new HashMap<String, JTextField>();
		JPanel configEntry = new JPanel();
		configEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
		configEntry.setLayout(new BoxLayout(configEntry, BoxLayout.PAGE_AXIS));
		
		InputStream notvalid_imageAsStream = this.getClass().getClassLoader().getResourceAsStream("picto-non-valide.png");   
		this.notvalid_image = ImageIO.read(notvalid_imageAsStream);
		
		InputStream valid_imageAsStream = this.getClass().getClassLoader().getResourceAsStream("picto-valide.png");   
		this.valid_image = ImageIO.read(valid_imageAsStream);
		
		for(Object key : EnumerationUtils.toList(properties.propertyNames())) {
			String strKey = (String) key;
			String errorMessage = "";
			JLabel label = new JLabel(strKey);
			label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			label.setAlignmentX(LEFT_ALIGNMENT);
			
		
			JTextField textField = new JTextField(render(properties.getProperty(strKey)));
			textField.setColumns(30);
			textField.setAlignmentX(LEFT_ALIGNMENT);
			textFields.put(strKey, textField);
			
			
			JPanel textButtonPanel = new JPanel();
			textButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
			textButtonPanel.setLayout(new BoxLayout(textButtonPanel, BoxLayout.LINE_AXIS));
			textButtonPanel.add(Box.createHorizontalGlue());
			
			JPanel iconPanel = new JPanel();

			JLabel iconValid = new JLabel(new ImageIcon(this.valid_image));
			JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
			
			JLabel errorLabel;
			if(strKey.contains(chromeDriverName)){
				errorMessage = errorMessageSelectFile;
				
				if( testWebAppDirectory(textField.getText(),false) ) {
					iconPanel.add(iconValid);
					errorLabel = new JLabel(" ");
				}
				else 
				{
					iconPanel.add(iconNotValid);
					errorLabel = new JLabel(errorMessage);
				}
			}
			else {
				errorMessage = errorMessageSelectURL;
				
				if( testWebAppURL(textField.getText(),false) ) {
					iconPanel.add(iconValid);
					errorLabel = new JLabel(" ");
				}
				else 
				{
					iconPanel.add(iconNotValid);
					errorLabel = new JLabel(errorMessage);
				}
			}
			
			textField.addKeyListener(new KeyListener() {
				public void keyPressed(
					KeyEvent a) {
			        if(a.getKeyCode()==KeyEvent.VK_ENTER)
			        {
			        	try {
							testIconValid(strKey, false);
							mainPane.repaint();
							mainPane.revalidate();
								
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
						}
			        }
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
			});

			JButton fileSearch = new JButton();
			fileSearch.setText("...");
			if(strKey.contains(chromeDriverName)){
				fileSearch.addActionListener(new ActionListener() {
	
					public void actionPerformed(ActionEvent arg0) {
						
						chooseFile(strKey);
						
					}
					
				});
			}

			if(strKey.contains(chromeDriverName)) {
				labelChrome = label;
				textFieldChrome = textField;
				iconPanelChrome = iconPanel;
				iconValidChrome = iconValid;
				iconNotValidChrome = iconNotValid;
				fileSearchChrome = fileSearch;
				textButtonPanelChrome = textButtonPanel;
				textButtonPanelChrome.add(fileSearchChrome);
				textButtonPanelChrome.add(textFieldChrome);
				textButtonPanelChrome.add(iconPanelChrome);
				errorLabelChrome = errorLabel;
			}
			if(strKey.contains(webAppName)) {
				labelWebApp = label;
				textFieldWebApp = textField;
				iconPanelWebApp = iconPanel;
				iconValidWebApp = iconValid;
				iconNotValidWebApp = iconNotValid;
				textButtonPanelWebApp = textButtonPanel;
				textButtonPanelWebApp.add(textFieldWebApp);
				textButtonPanelWebApp.add(iconPanelWebApp);
				errorLabelWebApp = errorLabel;
			}
			if(strKey.contains(recorderName)) {
				labelRecorder = label;
				textFieldRecorder = textField;
				iconPanelRecorder = iconPanel;
				iconValidRecorder = iconValid;
				iconNotValidRecorder = iconNotValid;
				textButtonPanelRecorder = textButtonPanel;
				textButtonPanelRecorder.add(textFieldRecorder);
				textButtonPanelRecorder.add(iconPanelRecorder);
				errorLabelRecorder = errorLabel;
			}
		}
		

		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton okButton = new JButton("Ok");
		
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(
				ActionEvent e) {
				saveAndClose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(
				ActionEvent e) {
				close();
			}
		});
		

		JButton tryButton = new JButton("Validate");
		tryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(Object key : EnumerationUtils.toList(properties.propertyNames())) 
				{
					String strKey = (String) key;
					try {
						testIconValid(strKey, false);
						mainPane.repaint();
						mainPane.revalidate();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}
			
		});
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		buttonPanel.add(tryButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);
		
		mainPane.add(labelChrome);
		mainPane.add(textButtonPanelChrome);
		mainPane.add(errorLabelChrome);

		mainPane.add(labelWebApp);
		mainPane.add(textButtonPanelWebApp);
		mainPane.add(errorLabelWebApp);
		
		mainPane.add(labelRecorder);
		mainPane.add(textButtonPanelRecorder);
		mainPane.add(errorLabelRecorder);
		
		mainPane.add(buttonPanel);
		
		
		setTitle("Settings");
		setSize(500, 500);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		
	}

	private String render(
		Object object) {
		if(object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

	private void saveAndClose() {
		for(Entry<String, JTextField> entry : textFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getText());
		}
		try {
			properties.store(FileUtils.openOutputStream(propertyFile), "Saved !");
		}
		catch(IOException e) {
			LOG.warn("Could not save properties", e);
		}
		close();
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
	}
	
	private void chooseFile(String strKey) {
		try {
	        JFileChooser dialogue = new JFileChooser();
	        dialogue.setDialogTitle("Directory to the chromeDriver");
	        dialogue.showOpenDialog(null);
	        
	        if(dialogue.getSelectedFile() != null)
	        {
		        if(dialogue.getSelectedFile().isDirectory())
		        {
					NotificationManager.showMessage("You choosed a Directory. You have to choose a File.").showNotification();
		        }
		        
		        if(dialogue.getSelectedFile().isFile())
		        {
			        LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
			        textFieldChrome.setText(dialogue.getSelectedFile().getAbsolutePath());
			    
					testIconValid(strKey, false);
		        }
	        }
	         
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	private void testIconValid(String strKey, boolean runTryValue) throws IOException {
		if(strKey.contains(webAppName))
		{
			if(!testWebAppURL(textFieldWebApp.getText(),runTryValue))
			{
				iconPanelWebApp.removeAll();
				iconPanelWebApp.add(iconNotValidWebApp);
				errorLabelWebApp.setText(errorMessageSelectURL);
			}
			else 
			{
				iconPanelWebApp.removeAll();
				iconPanelWebApp.add(iconValidWebApp);
				errorLabelWebApp.setText(" ");
			}
		}
		if(strKey.contains(recorderName))
		{
			if(!testWebAppURL(textFieldRecorder.getText(),runTryValue))
			{
				iconPanelRecorder.removeAll();
				iconPanelRecorder.add(iconNotValidRecorder);
				errorLabelRecorder.setText(errorMessageSelectURL);
			}
			else 
			{
				iconPanelRecorder.removeAll();
				iconPanelRecorder.add(iconValidRecorder);
				errorLabelRecorder.setText(" ");
			}
		}
		if(strKey.contains(chromeDriverName)) {
			if(!testWebAppDirectory(textFieldChrome.getText(),runTryValue))
			{
				iconPanelChrome.removeAll();
				iconPanelChrome.add(iconNotValidChrome);
				errorLabelChrome.setText(errorMessageSelectFile);
			}
			else 
			{
				iconPanelChrome.removeAll();
				iconPanelChrome.add(iconValidChrome);
				errorLabelChrome.setText(" ");
			}
		}
	}
		
	public static boolean testWebAppDirectory(String directory, boolean runTryValue) throws IOException{
		String fileName = directory.split("/")[directory.split("/").length - 1];
		
		if(directory.contains(" ")){
			if(runTryValue) 
    		{
				NotificationManager.showMessage(directory + " has spaces in its name.").showNotification();
    		}
			LOG.info("Status of " + directory + " : KO");
    		return false;
    	}
    	
    	if(testFileDirectory(directory, runTryValue, fileName)){
    		LOG.info("Status of " + directory + " : OK");
    		return true;
    	}
    	else {
    		
    		LOG.info("Status of " + directory + " : KO");
    		return false;
    	}
	}

	public static boolean testFileDirectory(String directory, boolean runTryValue, String fileName) {
		File myFile = new File(directory); 
		if(myFile.exists()) 
		{
			if(myFile.isFile())
			{
				return true;
			}
			else
			{
				if(runTryValue) 
	    		{
	    			NotificationManager.showMessage("You did not select a file.").showNotification();
	    		}
				return false;
			}
		} 
		else
		{
			if(runTryValue) 
    		{
    			NotificationManager.showMessage(fileName + " does not exist in the directory : " + directory.split(fileName)[0]).showNotification();
    		}
			return false;
		}
	}
	
	public static boolean testWebAppURL(String URL, boolean runTryValue) throws IOException{
		if(URL.contains(" ")){
			if(runTryValue) 
    		{
				NotificationManager.showMessage(URL + " has spaces in its name.").showNotification();
    		}
			LOG.info("Status of " + URL + " : KO");
    		return false;
    	}
    	
    	if(getStatus(URL)){
    		LOG.info("Status of " + URL + " : OK");
    		return true;
    	}
    	else {
    		if(runTryValue) 
    		{
    			NotificationManager.showMessage(URL + " does not answer.").showNotification();
    		}
    		LOG.info("Status of " + URL + " : KO");
    		return false;
    	}
	}
	
	public static boolean getStatus(String url) throws IOException {
		 
        boolean result = false;
        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
 
            int code = connection.getResponseCode();
            if (code == 200) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}