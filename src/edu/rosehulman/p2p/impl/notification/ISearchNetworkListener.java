package edu.rosehulman.p2p.impl.notification;

import edu.rosehulman.p2p.protocol.IListener;

public interface ISearchNetworkListener extends IListener {
	public static final String NAME = ISearchNetworkListener.class.getName();
	public static final String SEARCH_NETWORK_EVENT = "searchNetwork";

	public void searchNetwork();
}
