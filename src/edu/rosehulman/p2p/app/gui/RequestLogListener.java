package edu.rosehulman.p2p.app.gui;

import java.util.Collection;

import javax.swing.DefaultListModel;

import edu.rosehulman.p2p.impl.notification.IRequestLogListener;
import edu.rosehulman.p2p.protocol.IPacket;

public class RequestLogListener implements IRequestLogListener {

	private DefaultListModel<String> requestLogListModel;

	public RequestLogListener(DefaultListModel<String> requestLogListModel) {
		this.requestLogListModel = requestLogListModel;
	}

	@Override
	public void requestLogChanged(Collection<IPacket> packets) {
		this.requestLogListModel.clear();
		int i = 0;
		for (IPacket p : packets) {
			this.requestLogListModel.addElement(++i + " : " + p.getCommand() + " => " + p.getObject());
		}
	}

}
