package edu.rosehulman.p2p.impl.mediator;

import edu.rosehulman.p2p.protocol.IMediationHandler;
import edu.rosehulman.p2p.protocol.IP2PMediator;

public abstract class AbstractMediator implements IMediationHandler {

	protected IP2PMediator mediator;

	public AbstractMediator(IP2PMediator mediator) {
		this.mediator = mediator;
	}

}
