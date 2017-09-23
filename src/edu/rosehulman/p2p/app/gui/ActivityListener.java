package edu.rosehulman.p2p.app.gui;

import javax.swing.JTextArea;

import edu.rosehulman.p2p.impl.notification.IActivityListener;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class ActivityListener implements IActivityListener {

	private JTextArea statusTextArea;

	public ActivityListener(JTextArea statusTextArea) {
		this.statusTextArea = statusTextArea;
	}

	@Override
	public void activityPerformed(String message, IPacket p) {
		this.statusTextArea.append(message + p.getCommand() + IProtocol.LF);
		this.statusTextArea.setCaretPosition(this.statusTextArea.getDocument().getLength());
	}

}
