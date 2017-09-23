package edu.rosehulman.p2p.app.gui;

import javax.swing.JTextArea;

import edu.rosehulman.p2p.impl.notification.IDownloadListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IProtocol;

public class DownloadListener implements IDownloadListener {

	private JTextArea statusTextArea;

	public DownloadListener(JTextArea statusTextArea) {
		this.statusTextArea = statusTextArea;
	}

	@Override
	public void downloadComplete(IHost host, String file) {
		String msg = "Download of " + file + " from " + host + " complete!";
		this.statusTextArea.append(msg + IProtocol.LF);
		this.statusTextArea.setCaretPosition(this.statusTextArea.getDocument().getLength());
	}

}
