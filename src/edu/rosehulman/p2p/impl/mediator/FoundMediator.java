package edu.rosehulman.p2p.impl.mediator;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class FoundMediator extends AbstractMediator {
	public static final String NAME = FoundMediator.class.getName();

	public FoundMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(Object... args) throws P2PException {
		boolean found = (boolean) args[0];
		List<IHost> visited = (List<IHost>) args[1];
		IHost localhost = this.mediator.getLocalHost();
		int seqNum = (int) args[2];
		IHost finder = (IHost) args[3];
		Map<IHost, StreamMonitor> monitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IStreamMonitor streamMonitor = monitor.get(visited.get(visited.size() - 1));

		try {
			IPacket packet = new Packet(IProtocol.PROTOCOL, IProtocol.FOUND, visited.get(visited.size() - 1).toString());
			packet.setHeader(IProtocol.HOST, localhost.getHostAddress());
			packet.setHeader(IProtocol.PORT, localhost.getPort() + "");
			packet.setHeader(IProtocol.SEQ_NUM, seqNum + "");
			packet.setHeader(IProtocol.FILE_FOUND, found + "");
			packet.setHeader(IProtocol.FINDER_HOST, finder.toString());
			packet.setHeader(IProtocol.VISITED_PEERS, Host.listToString(visited));

			OutputStream out = streamMonitor.getOutputStream();
			packet.toStream(out);
		} catch (Exception e) {
			throw new P2PException(e);
		}
		return null;
	}

}
