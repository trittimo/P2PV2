package edu.rosehulman.p2p.impl.mediator;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class ListingMediator extends AbstractMediator {

	public static final String NAME = ListingMediator.class.getName();

	public ListingMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public Object handle(Object... args) throws P2PException {
		IHost remoteHost = (IHost) args[0];
		int seqNum = (int) args[1];
		Map<IHost, StreamMonitor> hostToInStreamMonitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IHost localhost = this.mediator.getLocalHost();

		IStreamMonitor monitor = hostToInStreamMonitor.get(remoteHost);

		if (monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}

		StringBuilder builder = new StringBuilder();
		File dir = new File(this.mediator.getRootDirectory());
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				builder.append(f.getName());
				builder.append(IProtocol.CRLF);
			}
		}

		try {
			byte[] payload = builder.toString().getBytes(IProtocol.CHAR_SET);

			IPacket packet = new Packet(IProtocol.PROTOCOL, IProtocol.LISTING, remoteHost.toString());
			packet.setHeader(IProtocol.HOST, localhost.getHostAddress());
			packet.setHeader(IProtocol.PORT, localhost.getPort() + "");
			packet.setHeader(IProtocol.SEQ_NUM, seqNum + "");
			packet.setHeader(IProtocol.PAYLOAD_SIZE, payload.length + "");

			OutputStream out = monitor.getOutputStream();
			packet.toStream(out);
			out.write(payload);
		} catch (Exception e) {
			throw new P2PException(e);
		}
		return null;
	}

}
