package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.ui.utils.PanelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;


public class FileChoosePanel extends AbstractPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3196229623723358193L;
	private static final Logger LOG = LogManager.getLogger(FileChoosePanel.class);
    private final String label;
    private final EnumError errorType;
    private JButton fileSearch;

    public FileChoosePanel(Properties properties, String strkey, String label, EnumError errorType) throws IOException {
        super(properties,strkey);
        this.label = label;
        this.errorType = errorType;
        super.setBasicProperties(strkey);
    }

    private void chooseDirectory(JTextField textField, String strKey) {
        JFileChooser dialogue = new JFileChooser(textField.getText());
        dialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialogue.setDialogTitle("Select directory for " + strKey);
        dialogue.showOpenDialog(null);

        if (dialogue.getSelectedFile() != null) {
            if (dialogue.getSelectedFile().isDirectory()) {
                LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
                textField.setText(dialogue.getSelectedFile().getAbsolutePath());
                try {
                    testIconValid(false);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    private void chooseFile(JTextField textField) {
        JFileChooser dialogue = new JFileChooser(textField.getText());
        dialogue.setDialogTitle(UIMessages.SELECT_FILE);
        dialogue.showOpenDialog(null);
        dialogue.setMaximumSize(getMaximumSize());

        if (dialogue.getSelectedFile() != null && dialogue.getSelectedFile().isFile()) {
            LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
            textField.setText(dialogue.getSelectedFile().getAbsolutePath());
            try {
                testIconValid(false);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
    private boolean testIconValidDirectory(boolean runTryValue) throws IOException {
        boolean fileOrDirectory = false;
        if(strkey.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH)) {
            fileOrDirectory = true;
        }
        return ConfigTesterHelper.testWebAppDirectory(textField.getText(),runTryValue, fileOrDirectory);
    }

    public void testIconValid(boolean runTry) throws IOException {
        boolean isNotKo = testIconValidDirectory(runTry);
        String errorMess = UIMessages.ERROR_FILE_NOT_EXIST;
        JLabel icon = iconNotValid;
        if(isNotKo) {
            LOG.info("Status of " + textField.getText() + " : OK");
            icon = iconValid;
            errorMess = " ";
        } else {
            LOG.info("Status of " + textField.getText() + " : KO");
        }
        iconPanel.removeAll();
        iconPanel.add(icon);
        errorLabel.setText(errorMess);
        this.repaint();
        this.revalidate();
    }

    public JButton createBasicFileSearch(JTextField textField, String strKey, Boolean fileOrDir) {
        JButton fileSearch = new JButton();
        fileSearch.setText("...");
        if (fileOrDir) {
            fileSearch.addActionListener(event ->  chooseFile(textField));
        } else {
            fileSearch.addActionListener(event -> chooseDirectory(textField, strKey));
        }
        return fileSearch;
    }

    protected JComponent buildPanel() throws IOException {
        fileSearch = createBasicFileSearch(textField, strkey, false);
        errorLabel = buildErrorLabel(iconPanel, errorType);
        this.setBorder(BorderFactory
                        .createTitledBorder(null, label,TitledBorder.LEFT,
                                TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
        return fileSearch;
    }
}
