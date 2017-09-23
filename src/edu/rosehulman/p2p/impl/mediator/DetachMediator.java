package edu.rosehulman.p2p.impl.mediator;

import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.notification.IConnectionListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class DetachMediator extends AbstractMediator {

	public static final String NAME = DetachMediator.class.getName();

	public DetachMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public Object handle(Object... args) throws P2PException {
		Map<IHost, IStreamMonitor> hostToInStreamMonitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IHost localhost = this.mediator.getLocalHost();
		synchronized (hostToInStreamMonitor) {
			if (!hostToInStreamMonitor.containsKey(args[0])) {
				return null;
			}

			IPacket sPacket = new Packet(IProtocol.PROTOCOL, IProtocol.DETACH, args[0].toString());
			sPacket.setHeader(IProtocol.HOST, localhost.getHostAddress());
			sPacket.setHeader(IProtocol.PORT, localhost.getPort() + "");

			IStreamMonitor monitor = hostToInStreamMonitor.remove(args[0]);
			Socket socket = monitor.getSocket();

			sPacket.toStream(monitor.getOutputStream());

			try {
				socket.close();
			} catch (Exception e) {
				Logger.getGlobal().log(Level.WARNING, "Error closing socket!", e);
			}

			this.mediator.fireEvent(IConnectionListener.NAME, IConnectionListener.CONNECTION_TERMINATED_EVENT, args[0]);
		}
		return null;
	}

}
