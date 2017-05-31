package io.toast.tk.agent.ui.panels;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.google.inject.Inject;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.WaiterThread;
import io.toast.tk.agent.ui.utils.PanelHelper;

public class DropPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3845489098917670302L;

	private AgentConfigProvider provider;
	
    private JPanel dp = new JPanel();

    private TransferHandler handler = new TransferHandler() {
        /**
		 * 
		 */
		private static final long serialVersionUID = -3596966166193452145L;

		public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            return true;
        }

        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            Transferable t = support.getTransferable();

            try {
                @SuppressWarnings("unchecked")
				java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

                for (File f : l) {
                	execute(f.toPath());
                }
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }
        
        private void execute(Path path) throws IOException {
        	Thread thread = new Thread(new WaiterThread(provider, path));
			thread.start();
        }
    };

    public void buildPanel() throws IOException {

		Image toastLogo = PanelHelper.createImage(this,"ToastLogo.png");
		this.setIconImage(toastLogo);
		
		JLabel titleLabel = PanelHelper.createBasicJLabel("DRAG & DROP");
		JLabel titleLabel2 = PanelHelper.createBasicJLabel("EXECUTE SCRIPT");
		JLabel imageLabel = new JLabel(new ImageIcon(toastLogo));       
		dp.setTransferHandler(handler);
		dp.add(titleLabel);
		dp.add(imageLabel);
		dp.add(titleLabel2);
		//dp.setOpaque(false);

        getContentPane().add(dp);
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setSize(170, 210);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.lowerFrame();
    }

	@Inject
	public DropPanel(AgentConfigProvider provider) throws IOException {
        super("Script dropper");
    	this.provider = provider;
		buildPanel();
    }

    private void lowerFrame() {

            Dimension windowSize = this.getSize();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Point centerPoint = ge.getCenterPoint();

            int dx = centerPoint.x * 2 - windowSize.width;
            int dy = centerPoint.y * 2 - windowSize.height;    
            this.setLocation(dx, dy);
    }
}
