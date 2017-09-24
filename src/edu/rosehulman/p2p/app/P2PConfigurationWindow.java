package edu.rosehulman.p2p.app;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class P2PConfigurationWindow {
	private JFrame mainFrame;
	private JDialog frame;

	private JTextField portField;
	private JTextField maxDepthField;
	private JLabel uriLbl;
	private JPanel pane;

	private String rootDir;

	public P2PConfigurationWindow(JFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.frame = new JDialog(mainFrame, "P2P Configuration Window", true);
		this.pane = new JPanel(new GridLayout(0, 2));
		this.frame.setContentPane(this.pane);

	}

	public void show() {
		JLabel portLbl = new JLabel("Port:");
		this.pane.add(portLbl);

		this.portField = new JTextField("9001");
		this.pane.add(this.portField);

		JLabel mdepthLabel = new JLabel("Max Search Depth: ");
		this.pane.add(mdepthLabel);
		this.maxDepthField = new JTextField();
		this.pane.add(this.maxDepthField);

		JLabel fileLbl = new JLabel("Root Directory:");
		this.pane.add(fileLbl);

		File f = new File(".");
		this.rootDir = f.getName();
		this.uriLbl = new JLabel("   " + this.rootDir + "/");
		this.pane.add(this.uriLbl);

		JButton fileButton = new JButton("Browse");
		this.pane.add(fileButton);

		fileButton.addActionListener(arg0 -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Select the root directory ");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(P2PConfigurationWindow.this.frame) == JFileChooser.APPROVE_OPTION) {
				File currentDir = chooser.getSelectedFile();
				P2PConfigurationWindow.this.uriLbl.setText("... /" + currentDir.getName());
				P2PConfigurationWindow.this.rootDir = currentDir.getAbsolutePath();
			}
		});

		JButton doneBtn = new JButton("Done");
		this.pane.add(doneBtn);
		doneBtn.addActionListener(arg0 -> {
			P2PConfigurationWindow.this.frame.setVisible(false);
			P2PConfigurationWindow.this.frame.dispose();
		});

		this.frame.pack();
		this.frame.setLocationRelativeTo(this.mainFrame);
		this.frame.setVisible(true);
	}

	public String getRootDirectory() {
		return this.rootDir;
	}

	public int getPort() {
		return Integer.parseInt(this.portField.getText());
	}

	public int getMaxDepth() {
		return Integer.parseInt(this.maxDepthField.getText());
	}
}
