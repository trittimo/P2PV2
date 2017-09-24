package edu.rosehulman.p2p.app.gui;

import javax.swing.JTextField;

import edu.rosehulman.p2p.impl.notification.ISearchNetworkListener;

public class SearchNetworkListener implements ISearchNetworkListener {
	private JTextField textField;

	public SearchNetworkListener(JTextField field) {
		this.textField = field;
	}

	@Override
	public void searchNetwork() {

	}
}
