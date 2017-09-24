package edu.rosehulman.p2p.impl.mediator;

import java.util.List;

import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.P2PException;

public class FindRequestMediator extends AbstractMediator {

	public static final String NAME = FindRequestMediator.class.getName();

	public FindRequestMediator(IP2PMediator mediator) {
		super(mediator);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(Object... args) throws P2PException {
		IHost remote = (IHost) args[0];
		int seqNum = (Integer) args[1];
		String filename = (String) args[2];
		int maxDepth = (Integer) args[3];
		List<IHost> visited = (List<IHost>) args[4];

		//TODO XD
		return null;
	}

}
