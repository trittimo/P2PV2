package edu.rosehulman.p2p.app.gui;

import javax.swing.DefaultListModel;

import edu.rosehulman.p2p.impl.notification.ISearchNetworkListener;
import edu.rosehulman.p2p.protocol.IHost;

public class SearchNetworkListener implements ISearchNetworkListener {

	private DefaultListModel<String> searchResultListModel;

	public SearchNetworkListener(DefaultListModel<String> searchResultList) {
		this.searchResultListModel = searchResultList;
	}

	@Override
	public void fileFound(String filename, IHost finder) {
		this.searchResultListModel.addElement(finder.toString());
	}

	@Override
	public void fileNotFound(String filename, IHost finder) {
		// Nothing to do here
	}
}
