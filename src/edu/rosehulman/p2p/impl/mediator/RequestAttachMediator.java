package edu.rosehulman.p2p.impl.mediator;

import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.P2PMediator;
import edu.rosehulman.p2p.impl.Packet;
import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class RequestAttachMediator extends AbstractMediator {
	public static final String NAME = RequestAttachMediator.class.getName();

	public RequestAttachMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public Object handle(Object... args) throws P2PException {
		IHost remoteHost = (IHost) args[0];
		Map<IHost, StreamMonitor> hostToInStreamMonitor = this.mediator.getSharedResource("hostToInStreamMonitor");
		IHost localhost = this.mediator.getLocalHost();

		synchronized (hostToInStreamMonitor) {
			if (hostToInStreamMonitor.containsKey(remoteHost)) {
				return false;
			}

			IPacket sPacket = new Packet(IProtocol.PROTOCOL, IProtocol.ATTACH, remoteHost.toString());
			sPacket.setHeader(IProtocol.HOST, localhost.getHostAddress());
			sPacket.setHeader(IProtocol.PORT, localhost.getPort() + "");
			int seqNum = ((P2PMediator) this.mediator).newSequenceNumber();
			sPacket.setHeader(IProtocol.SEQ_NUM, seqNum + "");

			try {
				this.mediator.logRequest(seqNum, sPacket);

				Socket socket = new Socket(remoteHost.getHostAddress(), remoteHost.getPort());
				sPacket.toStream(socket.getOutputStream());

				IPacket rPacket = new Packet();
				rPacket.fromStream(socket.getInputStream());
				if (rPacket.getCommand().equals(IProtocol.ATTACH_OK)) {
					// Connection accepted
					IStreamMonitor monitor = new StreamMonitor(this.mediator, remoteHost, socket);
					this.mediator.setConnected(remoteHost, monitor);

					// Let's start a thread for monitoring the input stream of this socket
					Thread runner = new Thread(monitor);
					runner.start();
				} else {
					// Connection rejected
					socket.close();
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, "Could not establish connection!", e);
				this.mediator.completeRequest(seqNum);
				return false;
			}
			this.mediator.completeRequest(seqNum);
			return true;
		}
	}

	public static class RequestAttachOKMediator extends AbstractMediator {
		public static final String NAME = RequestAttachOKMediator.class.getName();

		public RequestAttachOKMediator(IP2PMediator mediator) {
			super(mediator);
		}

		@Override
		public Object handle(Object... args) {
			IHost remoteHost = (IHost) args[0];
			Socket socket = (Socket) args[1];
			int seqNum = (int) args[2];

			IHost localhost = this.mediator.getSharedResource("localhost");

			IPacket sPacket = new Packet(IProtocol.PROTOCOL, IProtocol.ATTACH_OK, remoteHost.toString());
			sPacket.setHeader(IProtocol.HOST, localhost.getHostAddress());
			sPacket.setHeader(IProtocol.PORT, localhost.getPort() + "");
			sPacket.setHeader(IProtocol.SEQ_NUM, seqNum + "");

			try {
				sPacket.toStream(socket.getOutputStream());

				IStreamMonitor monitor = new StreamMonitor(this.mediator, remoteHost, socket);
				this.mediator.setConnected(remoteHost, monitor);

				// Let's start a thread for monitoring the input stream of this socket
				Thread runner = new Thread(monitor);
				runner.start();
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, "Could not send attach ok message to remote peer", e);
			}
			return null;
		}

	}

	public static class RequestAttachNOKMediator extends AbstractMediator {
		public static final String NAME = RequestAttachNOKMediator.class.getName();

		public RequestAttachNOKMediator(IP2PMediator mediator) {
			super(mediator);
		}

		@Override
		public Object handle(Object... args) {
			// IHost remoteHost, Socket socket, int seqNum
			IHost remoteHost = (IHost) args[0];
			Socket socket = (Socket) args[1];
			int seqNum = (int) args[2];

			IHost localhost = this.mediator.getSharedResource("localhost");

			IPacket sPacket = new Packet(IProtocol.PROTOCOL, IProtocol.ATTACH_NOK, remoteHost.toString());
			sPacket.setHeader(IProtocol.HOST, localhost.getHostAddress());
			sPacket.setHeader(IProtocol.PORT, localhost.getPort() + "");
			sPacket.setHeader(IProtocol.SEQ_NUM, seqNum + "");

			try {
				sPacket.toStream(socket.getOutputStream());
				socket.close();
				Logger.getGlobal().log(Level.INFO, "Connection rejected to " + remoteHost);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, "Could not send attach ok message to remote peer", e);
			}
			return null;
		}

	}
}
