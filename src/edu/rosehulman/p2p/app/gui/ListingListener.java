package edu.rosehulman.p2p.app.gui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import edu.rosehulman.p2p.impl.notification.IListingListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IProtocol;

public class ListingListener implements IListingListener {

	private DefaultListModel<String> fileListModel;
	private JTextArea statusTextArea;

	public ListingListener(DefaultListModel<String> fileListModel, JTextArea statusTextArea) {
		this.fileListModel = fileListModel;
		this.statusTextArea = statusTextArea;
	}

	@Override
	public void listingReceived(IHost host, List<String> listing) {
		String msg = "File listing received from " + host + "!";
		this.statusTextArea.append(msg + IProtocol.LF);
		this.statusTextArea.setCaretPosition(this.statusTextArea.getDocument().getLength());

		this.fileListModel.clear();
		for (String f : listing) {
			this.fileListModel.addElement(f);
		}
	}

}
