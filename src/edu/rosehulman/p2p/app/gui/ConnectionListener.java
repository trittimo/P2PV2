package edu.rosehulman.p2p.app.gui;

import javax.swing.DefaultListModel;

import edu.rosehulman.p2p.impl.notification.IConnectionListener;
import edu.rosehulman.p2p.protocol.IHost;

public class ConnectionListener implements IConnectionListener {

	private DefaultListModel<IHost> peerListModel;

	public ConnectionListener(DefaultListModel<IHost> peerListModel) {
		this.peerListModel = peerListModel;
	}

	@Override
	public void connectionEstablished(IHost host) {
		this.peerListModel.addElement(host);
	}

	@Override
	public void connectionTerminated(IHost host) {
		this.peerListModel.removeElement(host);
	}

}
