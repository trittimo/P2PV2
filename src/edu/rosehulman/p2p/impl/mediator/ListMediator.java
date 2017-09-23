package edu.rosehulman.p2p.impl.mediator;

import java.util.Map;

import edu.rosehulman.p2p.impl.P2PMediator;
import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class ListMediator extends AbstractMediator {

	public static final String NAME = ListMediator.class.getName();

	public ListMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public Object handle(Object... args) throws P2PException {
		Map<IHost, StreamMonitor> hostToInStreamMonitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IHost remoteHost = (IHost) args[0];
		IStreamMonitor monitor = hostToInStreamMonitor.get(remoteHost);
		IHost localhost = this.mediator.getLocalHost();

		if (monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}

		int seqNum = ((P2PMediator) this.mediator).newSequenceNumber();
		IPacket packet = new Packet(IProtocol.PROTOCOL, IProtocol.LIST, remoteHost.toString());
		packet.setHeader(IProtocol.HOST, localhost.getHostAddress());
		packet.setHeader(IProtocol.PORT, localhost.getPort() + "");
		packet.setHeader(IProtocol.SEQ_NUM, seqNum + "");

		this.mediator.logRequest(seqNum, packet);
		packet.toStream(monitor.getOutputStream());
		return null;
	}

}
