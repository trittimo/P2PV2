/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Chandan R. Rupakheti (chandan.rupakheti@rose-hulman.edu)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.rosehulman.p2p.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import edu.rosehulman.p2p.app.gui.ActivityListener;
import edu.rosehulman.p2p.app.gui.ConnectionListener;
import edu.rosehulman.p2p.app.gui.DownloadListener;
import edu.rosehulman.p2p.app.gui.ListingListener;
import edu.rosehulman.p2p.app.gui.RequestLogListener;
import edu.rosehulman.p2p.app.gui.SearchNetworkListener;
import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.mediator.DetachMediator;
import edu.rosehulman.p2p.impl.mediator.FindMediator;
import edu.rosehulman.p2p.impl.mediator.GetMediator;
import edu.rosehulman.p2p.impl.mediator.ListMediator;
import edu.rosehulman.p2p.impl.mediator.RequestAttachMediator;
import edu.rosehulman.p2p.impl.notification.IActivityListener;
import edu.rosehulman.p2p.impl.notification.IConnectionListener;
import edu.rosehulman.p2p.impl.notification.IDownloadListener;
import edu.rosehulman.p2p.impl.notification.IListingListener;
import edu.rosehulman.p2p.impl.notification.IRequestLogListener;
import edu.rosehulman.p2p.impl.notification.ISearchNetworkListener;
import edu.rosehulman.p2p.protocol.IConnectionMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IProtocol;

/**
 * @author rupakhet
 *
 */
public class P2PGUI {
	JFrame frame;
	JPanel contentPane;

	JPanel newConnectionPanel;
	JTextField hostNameField;
	JTextField portField;
	JButton connectButton;

	JPanel peersPanel;
	JScrollPane peerListScrollPane;
	JList<IHost> peerList;
	DefaultListModel<IHost> peerListModel;
	JButton disconnectButton;
	JButton listFileButton;
	JScrollPane fileListingPane;
	JList<String> fileList;
	DefaultListModel<String> fileListModel;
	JButton downloadDirect;

	JPanel statusPanel;
	JScrollPane statusScrollPane;
	JTextArea statusTextArea;
	JScrollPane requestLogScrollPane;
	DefaultListModel<String> requestLogListModel;
	JList<String> requestLogList;

	JPanel searchFilePanel;
	JTextField searchTermField;
	JButton searchButton;
	JList<String> searchResultList;
	DefaultListModel<String> searchResultListModel;
	JScrollPane searchResultScrollPane;
	JButton downloadAfterSearch;

	JPanel networkMapPanel;

	IP2PMediator mediator;
	IConnectionMonitor connectionMonitor;

	public P2PGUI(JFrame mainFrame, IP2PMediator mediator, IConnectionMonitor connectionMonitor) {
		this.frame = mainFrame;
		this.mediator = mediator;
		this.connectionMonitor = connectionMonitor;
		initGUI();
		initListeners();
	}

	public IActivityListener activityListener;
	public IConnectionListener connectionListener;
	public IDownloadListener downloadListener;
	public IListingListener listingListener;
	public IRequestLogListener requestLogListener;
	public ISearchNetworkListener searchNetworkListener;

	private void initListeners() {
		this.activityListener = new ActivityListener(this.statusTextArea);
		this.connectionListener = new ConnectionListener(this.peerListModel);
		this.downloadListener = new DownloadListener(this.statusTextArea);
		this.listingListener = new ListingListener(this.fileListModel, this.statusTextArea);
		this.requestLogListener = new RequestLogListener(this.requestLogListModel);
		this.searchNetworkListener = new SearchNetworkListener(this.searchTermField);
	}

	public void show() {
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				P2PGUI.this.connectionMonitor.stop();
			}
		});

		// Position the window to the center of the screen
		this.frame.pack();
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = toolkit.getScreenSize();
		final int x = (screenSize.width - this.frame.getWidth()) / 2;
		final int y = (screenSize.height - this.frame.getHeight()) / 2;
		this.frame.setLocation(x, y);
	}

	private void initGUI() {
		this.frame.setTitle("Rose P2P App (" + IProtocol.PROTOCOL + ") - Localhost [" + this.mediator.getLocalHost() + "]");
		this.contentPane = (JPanel) this.frame.getContentPane();

		createNetworkMapPanel();
		createSearchPanel();
		createStatusPanel();
		configurePeersPanel();

		this.contentPane.add(this.peersPanel, BorderLayout.WEST);
		this.contentPane.add(this.networkMapPanel, BorderLayout.CENTER);
		this.contentPane.add(this.searchFilePanel, BorderLayout.EAST);
		this.contentPane.add(this.statusPanel, BorderLayout.SOUTH);
	}

	private void configurePeersPanel() {
		this.peersPanel = new JPanel(new BorderLayout());
		this.peersPanel.setBorder(BorderFactory.createTitledBorder("Remote Connections"));

		this.newConnectionPanel = new JPanel();

		this.hostNameField = new JTextField("");
		this.hostNameField.setColumns(25);

		this.portField = new JTextField("");
		this.portField.setColumns(8);

		this.connectButton = new JButton("Connect");
		this.newConnectionPanel.add(new JLabel("Host: "));
		this.newConnectionPanel.add(this.hostNameField);
		this.newConnectionPanel.add(new JLabel("Port: "));
		this.newConnectionPanel.add(this.portField);
		this.newConnectionPanel.add(this.connectButton);

		this.connectButton.addActionListener(e -> {
			try {
				String host = P2PGUI.this.hostNameField.getText();
				int port = Integer.parseInt(P2PGUI.this.portField.getText());
				final IHost remoteHost = new Host(host, port);

				Thread runner = new Thread() {
					@Override
					public void run() {
						postStatus("Trying to connect to " + remoteHost + " ...");
						try {
							if ((Boolean) P2PGUI.this.mediator.mediate(RequestAttachMediator.NAME, remoteHost)) {
								postStatus("Connected to " + remoteHost);
							} else {
								postStatus("Could not connect to " + remoteHost + ". Please try again!");
							}
						} catch (Exception exp) {
							postStatus("An error occured while connecting: " + exp.getMessage());
							exp.printStackTrace();
						}
					}
				};
				runner.start();
			} catch (Exception ex) {
				postStatus("Connection could not be established: " + ex.getMessage());
				ex.printStackTrace();
			}
		});

		this.peersPanel.add(this.newConnectionPanel, BorderLayout.NORTH);

		JPanel peerListPanel = new JPanel(new BorderLayout());
		peerListPanel.add(new JLabel("List of Peers", JLabel.CENTER), BorderLayout.NORTH);
		this.peerListModel = new DefaultListModel<>();
		this.peerList = new JList<>(this.peerListModel);
		this.peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.peerListScrollPane = new JScrollPane(this.peerList);
		this.listFileButton = new JButton("List Files");
		this.disconnectButton = new JButton("Disconnect");
		peerListPanel.add(this.peerListScrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.add(this.disconnectButton);
		buttonPanel.add(this.listFileButton);
		peerListPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.disconnectButton.addActionListener(e -> {
			IHost remoteHost = P2PGUI.this.peerList.getSelectedValue();
			if (remoteHost == null) {
				JOptionPane.showMessageDialog(	P2PGUI.this.frame,
												"You must first select a peer from the list above!",
												"P2P Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				P2PGUI.this.mediator.mediate(DetachMediator.NAME, remoteHost);
				postStatus("Disconnected from " + remoteHost + "!");
			} catch (Exception ex) {
				postStatus("Error disconnecting to " + remoteHost + "!");
				ex.printStackTrace();
			}
		});

		this.listFileButton.addActionListener(e -> {
			final IHost remoteHost = P2PGUI.this.peerList.getSelectedValue();
			if (remoteHost == null) {
				JOptionPane.showMessageDialog(	P2PGUI.this.frame,
												"You must first select a peer from the list above!",
												"P2P Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						P2PGUI.this.mediator.mediate(ListMediator.NAME, remoteHost);
						postStatus("File listing request sent to " + remoteHost + "!");
					} catch (Exception e) {
						postStatus("Error sending list request to " + remoteHost + "!");
						e.printStackTrace();
					}
				}
			};
			thread.start();
		});

		JPanel fileListPanel = new JPanel(new BorderLayout());
		fileListPanel.add(new JLabel("List of files in the selected peer", JLabel.CENTER), BorderLayout.NORTH);
		this.fileListModel = new DefaultListModel<>();
		this.fileList = new JList<>(this.fileListModel);
		this.fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fileListingPane = new JScrollPane(this.fileList);
		this.downloadDirect = new JButton("Download the selected file");
		fileListPanel.add(this.fileListingPane, BorderLayout.CENTER);
		fileListPanel.add(this.downloadDirect, BorderLayout.SOUTH);

		this.downloadDirect.addActionListener(e -> {
			final IHost remoteHost = P2PGUI.this.peerList.getSelectedValue();
			final String fileName = P2PGUI.this.fileList.getSelectedValue();
			if (remoteHost == null || fileName == null) {
				JOptionPane.showMessageDialog(	P2PGUI.this.frame,
												"You must have a peer and a file selected from the lists above!",
												"P2P Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						P2PGUI.this.mediator.mediate(GetMediator.NAME, remoteHost, fileName);
						postStatus("Getting file " + fileName + " from " + remoteHost + "...");
					} catch (Exception e) {
						postStatus("Error sending the get file request to " + remoteHost + "!");
						e.printStackTrace();
					}
				}
			};
			thread.start();
		});

		this.searchButton.addActionListener(e -> {
			ArrayList<IHost> peers = new ArrayList<>();
			for (int i = 0; i < this.peerList.getModel().getSize(); i++) {
				peers.add(this.peerList.getModel().getElementAt(i));
			}
			String filename = P2PGUI.this.searchTermField.getText();
			if (peers.size() == 0 || filename == null || filename.isEmpty()) {
				JOptionPane.showMessageDialog(	P2PGUI.this.frame,
												"You must have connected to at least one peer and a search term",
												"P2P Error",
												JOptionPane.ERROR_MESSAGE);
				return;
			}
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						P2PGUI.this.mediator.mediate(FindMediator.NAME, peers, filename);
						postStatus("Searching network of peers for " + filename);
					} catch (Exception e) {
						postStatus("Error searching network of peers!");
						e.printStackTrace();
					}
				}
			};
			thread.start();
		});

		this.peersPanel.add(peerListPanel, BorderLayout.WEST);
		this.peersPanel.add(fileListPanel, BorderLayout.CENTER);
	}

	private void createStatusPanel() {
		this.statusPanel = new JPanel(new BorderLayout());
		this.statusPanel.setBorder(BorderFactory.createTitledBorder("Activity"));

		JPanel panel = new JPanel(new BorderLayout());
		this.statusTextArea = new JTextArea("");
		this.statusTextArea.setRows(10);
		this.statusScrollPane = new JScrollPane(this.statusTextArea);
		panel.add(new JLabel("Activity Log", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(this.statusScrollPane, BorderLayout.CENTER);
		this.statusPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new BorderLayout());
		this.requestLogListModel = new DefaultListModel<>();
		this.requestLogList = new JList<>(this.requestLogListModel);
		this.requestLogScrollPane = new JScrollPane(this.requestLogList);

		panel.add(new JLabel("Request Log", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(this.requestLogScrollPane, BorderLayout.CENTER);
		this.statusPanel.add(panel, BorderLayout.EAST);
	}

	private void createNetworkMapPanel() {
		this.networkMapPanel = new JPanel(new BorderLayout());
		this.networkMapPanel.setBorder(BorderFactory.createTitledBorder("Network Graph"));

		this.networkMapPanel.add(new JLabel("Shown the network graph (Bonus) ..."));
	}

	private void createSearchPanel() {
		this.searchFilePanel = new JPanel(new BorderLayout());
		this.searchFilePanel.setBorder(BorderFactory.createTitledBorder("Network File Searching"));

		JPanel top = new JPanel();
		top.add(new JLabel("Search Term: "));
		this.searchTermField = new JTextField("");
		this.searchTermField.setColumns(15);
		this.searchButton = new JButton("Search Network");
		top.add(this.searchTermField);
		top.add(this.searchButton);

		this.searchResultListModel = new DefaultListModel<>();
		this.searchResultList = new JList<>(this.searchResultListModel);
		this.searchResultScrollPane = new JScrollPane(this.searchResultList);

		this.downloadAfterSearch = new JButton("Download the selected file");

		this.searchFilePanel.add(top, BorderLayout.NORTH);
		this.searchFilePanel.add(this.searchResultScrollPane, BorderLayout.CENTER);
		this.searchFilePanel.add(this.downloadAfterSearch, BorderLayout.SOUTH);
	}

	private void postStatus(String msg) {
		this.statusTextArea.append(msg + IProtocol.LF);
		this.statusTextArea.setCaretPosition(this.statusTextArea.getDocument().getLength());
	}
}
