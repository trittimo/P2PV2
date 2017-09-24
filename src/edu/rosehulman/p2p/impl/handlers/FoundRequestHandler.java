package edu.rosehulman.p2p.impl.handlers;

import java.io.InputStream;
import java.util.ArrayList;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.mediator.FoundMediator;
import edu.rosehulman.p2p.impl.notification.ISearchNetworkListener;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

public class FoundRequestHandler extends AbstractHandler implements IRequestHandler {

	public FoundRequestHandler(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public void handle(IPacket packet, InputStream in) throws P2PException {
		int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
		String filename = packet.getHeader(IProtocol.FILE_NAME);
		boolean fileFound = Boolean.valueOf(packet.getHeader(IProtocol.FILE_FOUND));
		IHost finder = Host.fromString(packet.getHeader(IProtocol.FINDER_HOST));
		ArrayList<IHost> visited = new ArrayList<IHost>();

		for (String v : packet.getHeader(IProtocol.VISITED_PEERS).split(IProtocol.PEER_SEPARATOR)) {
			visited.add(Host.fromString(v));
		}

		if (visited.size() == 1) {
			if (fileFound) {
				this.mediator.fireEvent(ISearchNetworkListener.NAME,
										ISearchNetworkListener.FILE_FOUND_EVENT,
										filename,
										finder);
			} else {
				this.mediator.fireEvent(ISearchNetworkListener.NAME,
										ISearchNetworkListener.FILE_NOT_FOUND_EVENT,
										filename,
										finder);
			}
			return;
		}

		visited.remove(this.mediator.getLocalHost());
		IHost target = visited.get(visited.size() - 1);

		this.mediator.mediate(FoundMediator.NAME, fileFound, visited, seqNum, finder);
	}

}
