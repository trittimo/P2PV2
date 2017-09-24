package edu.rosehulman.p2p.impl.mediator;

import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.P2PMediator;
import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class FindMediator extends AbstractMediator {
	public static final String NAME = FindMediator.class.getName();

	public FindMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(Object... args) throws P2PException {
		Map<IHost, StreamMonitor> monitors = this.mediator.getSharedResource("hostToInStreamMonitor");
		List<IHost> peers = (List<IHost>) args[0];
		String filename = (String) args[1];
		List<IHost> visited = (List<IHost>) args[2];
		int maxDepth = (Integer) args[3];
		int currentDepth = (Integer) args[4];

		IHost localhost = this.mediator.getLocalHost();

		for (IHost peer : peers) {
			// make sure not already visited
			if (visited.contains(peer)) {
				System.out.println("Already visited " + peer);
				continue;
			}
			IStreamMonitor monitor = monitors.get(peer);
			if (monitor == null) {
				throw new P2PException("No connection exists to " + peer);
			}

			int seqNum = ((P2PMediator) this.mediator).newSequenceNumber();
			IPacket packet = new Packet(IProtocol.PROTOCOL, IProtocol.FIND, peer.toString());
			packet.setHeader(IProtocol.HOST, localhost.getHostAddress());
			packet.setHeader(IProtocol.PORT, localhost.getPort() + "");
			packet.setHeader(IProtocol.SEQ_NUM, seqNum + "");
			packet.setHeader(IProtocol.FILE_NAME, filename);
			packet.setHeader(IProtocol.MAX_DEPTH, maxDepth + "");
			packet.setHeader(IProtocol.CURRENT_DEPTH, currentDepth + 1 + "");
			packet.setHeader(IProtocol.VISITED_PEERS, Host.listToString(visited));

			this.mediator.logRequest(seqNum, packet);
			packet.toStream(monitor.getOutputStream());
		}

		return null;
	}

}
