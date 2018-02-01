package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.ui.utils.PanelHelper;
import io.toast.tk.runtime.constant.Property;
import io.toast.tk.runtime.parse.FileHelper;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Report panel
 */
public class ReportPanel extends AbstractFrame {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LogManager.getLogger(ReportPanel.class);
	
	
	public ReportPanel() throws IOException {
		super();

		buildContentPanel();
		
	    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);        
         
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	
	private void buildContentPanel() throws IOException {
        JPanel mainPane = buildMainPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
        
		Image toastLogo = PanelHelper.createImage(this, "ToastLogo.png");
        
        // add the panel to this frame        
		this.setContentPane(mainPane);
		this.setIconImage(toastLogo);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private JPanel buildMainPanel() throws IOException {
		JPanel mainPane = PanelHelper.createBasicJPanel("Report List", BoxLayout.PAGE_AXIS);
		
		Dimension prefDim = new Dimension(400, 400);
		Dimension moyDim = new Dimension(350, 350);

		mainPane.setPreferredSize(prefDim);
		mainPane.setMinimumSize(moyDim);

		JScrollPane _scroll = new JScrollPane();
		_scroll.setViewportView(buildFilePanel());
		mainPane.add(_scroll);

		return mainPane;
	}
	
	private JPanel buildFilePanel() throws IOException {
		JPanel filePane = PanelHelper.createBasicJPanel();
		
		filePane.setBackground(Color.white);
		filePane.setAlignmentX(LEFT_ALIGNMENT);
		filePane.setLayout(new BoxLayout(filePane, BoxLayout.PAGE_AXIS));
		filePane.add(Box.createHorizontalGlue());
		filePane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		
		String directoryPath = Property.TOAST_TARGET_DIR;
		List<File> files = Arrays.asList(FileHelper.findFiles(directoryPath));
		organizeFiles(files);
		
		for(File file : files) {
			filePane.add(buildFileButton(file));
		}
		
		return filePane;
	}
	
	private JButton buildFileButton(File file) {
		ImageIcon toastIcon = PanelHelper.createImageIcon(this, "report_icon_24.png");
		JButton fileReport = new JButton(toastIcon);
		fileReport.setBackground(Color.white);

		fileReport.setBorder(BorderFactory.createEmptyBorder());
		
		fileReport.setText(file.getName());
		fileReport.addActionListener(event ->  {
			try {
				openReport(file);
			} catch (Exception e) {
				LOG.error(e);
			}
		});
        return fileReport;
	}
	
	private void organizeFiles(List<File> file) {
		Comparator<File> comparator = new Comparator<File>() {
		    @Override
		    public int compare(File left, File right) {
		    	Long valueLeft = left.lastModified();
		    	Long valueRight = right.lastModified();
		        if(valueLeft < valueRight) {
		        	return 1;
		        } else if(valueLeft == valueRight) {
		        	return 0;
		        } else {
		        	return -1;
		        }
		    }
		};
		
		Collections.sort(file, comparator);
	}
	
	private void openReport(File file) throws IOException {
		Desktop.getDesktop().browse(file.toURI());
	}
	
}