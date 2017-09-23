package edu.rosehulman.p2p.protocol;

public interface IMediationHandler {
	public Object handle(Object... args) throws P2PException;
}
