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

public class GetMediator extends AbstractMediator {

	public static final String NAME = GetMediator.class.getName();

	public GetMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public Object handle(Object... args) throws P2PException {
		IHost remoteHost = (IHost) args[0];
		String file = (String) args[1];
		Map<IHost, StreamMonitor> hostToInStreamMonitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IHost localhost = this.mediator.getLocalHost();

		IStreamMonitor monitor = hostToInStreamMonitor.get(remoteHost);

		if (monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}

		int seqNum = ((P2PMediator) this.mediator).newSequenceNumber();
		IPacket packet = new Packet(IProtocol.PROTOCOL, IProtocol.GET, remoteHost.toString());
		packet.setHeader(IProtocol.HOST, localhost.getHostAddress());
		packet.setHeader(IProtocol.PORT, localhost.getPort() + "");
		packet.setHeader(IProtocol.SEQ_NUM, seqNum + "");
		packet.setHeader(IProtocol.FILE_NAME, file);

		this.mediator.logRequest(seqNum, packet);
		packet.toStream(monitor.getOutputStream());

		return null;
	}

}
