package edu.rosehulman.p2p.impl.handlers;

import java.io.InputStream;
import java.util.ArrayList;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.mediator.FindRequestMediator;
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
		String host = packet.getHeader(IProtocol.HOST);
		int port = Integer.parseInt(packet.getHeader(IProtocol.PORT));
		IHost remoteHost = new Host(host, port);
		String filename = packet.getHeader(IProtocol.FILE_NAME);
		int maxDepth = Integer.parseInt(packet.getHeader(IProtocol.MAX_DEPTH));
		ArrayList<IHost> visited = new ArrayList<IHost>();

		for (String v : packet.getHeader(IProtocol.VISITED_PEERS).split(IProtocol.PEER_SEPARATOR)) {
			visited.add(Host.fromString(v));
		}

		// If file here, found mediator
		// else, find mediator

		// can just delete findrequestmediator since we will just use FindMediator

		// Also, make sure deal with depth
		this.mediator.mediate(FindRequestMediator.NAME, remoteHost, seqNum, filename, maxDepth, visited);
	}

}
