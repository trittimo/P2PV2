package edu.rosehulman.p2p.impl.handlers;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.impl.mediator.FindMediator;
import edu.rosehulman.p2p.impl.mediator.FoundMediator;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

public class FindRequestHandler extends AbstractHandler implements IRequestHandler {

	public FindRequestHandler(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public void handle(IPacket packet, InputStream in) throws P2PException {
		int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
		String filename = packet.getHeader(IProtocol.FILE_NAME);
		int maxDepth = Integer.parseInt(packet.getHeader(IProtocol.MAX_DEPTH));
		int currentDepth = Integer.parseInt(packet.getHeader(IProtocol.CURRENT_DEPTH));
		ArrayList<IHost> visited = new ArrayList<IHost>();

		for (String v : packet.getHeader(IProtocol.VISITED_PEERS).split(IProtocol.PEER_SEPARATOR)) {
			visited.add(Host.fromString(v));
		}

		File dir = new File(this.mediator.getRootDirectory());
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().equals(filename)) {
				System.out.println("Found the file");
				this.mediator.mediate(FoundMediator.NAME, true, visited, seqNum, this.mediator.getLocalHost());
				// found mediator
				// TODO consider whether remove the return
			}
		}

		List<IHost> peers = new ArrayList<IHost>();
		Map<IHost, StreamMonitor> hosts = this.mediator.getSharedResource("hostToInStreamMonitor");
		peers.addAll(hosts.keySet());

		boolean cannotContinue = currentDepth >= maxDepth || peers.size() == 0;
		boolean allPeersVisited = true;
		for (IHost peer : peers) {
			if (visited.contains(peer)) {
				allPeersVisited &= true;
			} else {
				allPeersVisited = false;
			}
		}

		if (cannotContinue || allPeersVisited) {
			System.out.println("Stopping search here: " + this.mediator.getLocalHost());
			this.mediator.mediate(FoundMediator.NAME, false, visited, seqNum, this.mediator.getLocalHost());
			return;
		}

		visited.add(this.mediator.getLocalHost());
		this.mediator.mediate(FindMediator.NAME, peers, filename, visited, maxDepth, currentDepth);
	}

}
