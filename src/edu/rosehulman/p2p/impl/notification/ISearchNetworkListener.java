package edu.rosehulman.p2p.impl.notification;

import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IListener;

public interface ISearchNetworkListener extends IListener {
	public static final String NAME = ISearchNetworkListener.class.getName();
	public static final String FILE_FOUND_EVENT = "fileFound";
	public static final String FILE_NOT_FOUND_EVENT = "fileNotFound";

	public void fileFound(String filename, IHost finder);

	public void fileNotFound(String filename, IHost finder);
}
