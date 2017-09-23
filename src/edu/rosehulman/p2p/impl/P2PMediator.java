/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Chandan R. Rupakheti (chandan.rupakheti@rose-hulman.edu)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.rosehulman.p2p.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.notification.IActivityListener;
import edu.rosehulman.p2p.impl.notification.IConnectionListener;
import edu.rosehulman.p2p.impl.notification.IDownloadListener;
import edu.rosehulman.p2p.impl.notification.IListingListener;
import edu.rosehulman.p2p.impl.notification.IRequestLogListener;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IListener;
import edu.rosehulman.p2p.protocol.IMediationHandler;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class P2PMediator implements IP2PMediator {
	private HashMap<String, Object> sharedResources;
	private HashMap<String, IMediationHandler> mediationHandlers;
	private HashMap<String, List<IListener>> listeners;

	public P2PMediator(int port, String rootDirectory) throws UnknownHostException {
		this.mediationHandlers = new HashMap<>();
		this.listeners = new HashMap<>();
		this.sharedResources = new HashMap<>();

		this.addSharedResource("localhost", new Host(InetAddress.getLocalHost().getHostAddress(), port));
		this.addSharedResource("rootDirectory", rootDirectory);
		this.addSharedResource(	"hostToInStreamMonitor",
								Collections.synchronizedMap(new HashMap<IHost, IStreamMonitor>()));
		this.addSharedResource("requestLog", Collections.synchronizedMap(new HashMap<Integer, IPacket>()));
		this.addSharedResource("downloadListeners", Collections.synchronizedList(new ArrayList<IDownloadListener>()));
		this.addSharedResource("listingListeners", Collections.synchronizedList(new ArrayList<IListingListener>()));
		this.addSharedResource(	"requestLogListeners",
								Collections.synchronizedList(new ArrayList<IRequestLogListener>()));
		this.addSharedResource(	"connectionListeners",
								Collections.synchronizedList(new ArrayList<IConnectionListener>()));
		this.addSharedResource("activityListeners", Collections.synchronizedList(new ArrayList<IActivityListener>()));
		this.addSharedResource("sequence", 0);

	}

	public synchronized int newSequenceNumber() {
		Integer sequence = this.getSharedResource("sequence");
		this.addSharedResource("sequence", ++sequence);
		return sequence;
	}

	@Override
	public Host getLocalHost() {
		return this.getSharedResource("localhost");
	}

	@Override
	public String getRootDirectory() {
		return this.getSharedResource("rootDirectory");
	}

	@Override
	public void setConnected(IHost host, IStreamMonitor monitor) {
		Map<IHost, IStreamMonitor> hostToInStreamMonitor = this.getSharedResource("hostToInStreamMonitor");
		hostToInStreamMonitor.put(host, monitor);
		fireEvent(IConnectionListener.NAME, IConnectionListener.CONNECTION_ESTABLISHED_EVENT, host);
	}

	@Override
	public IPacket getRequest(int number) {
		Map<Integer, IPacket> requestLog = this.getSharedResource("requestLog");
		return requestLog.get(number);
	}

	@Override
	public void logRequest(int number, IPacket p) {
		Map<Integer, IPacket> requestLog = this.getSharedResource("requestLog");
		requestLog.put(number, p);
		fireEvent(	IRequestLogListener.NAME,
					IRequestLogListener.REQUEST_LOG_CHANGED_EVENT,
					Collections.unmodifiableCollection(requestLog.values()));
	}

	@Override
	public void completeRequest(int number) {
		Map<Integer, IPacket> requestLog = this.getSharedResource("requestLog");
		IPacket p = requestLog.remove(number);

		if (p != null) {
			fireEvent(	IRequestLogListener.NAME,
						IRequestLogListener.REQUEST_LOG_CHANGED_EVENT,
						Collections.unmodifiableCollection(requestLog.values()));
		}
	}

	@Override
	public void addMediationHandler(String handlerName, IMediationHandler handler) {
		this.mediationHandlers.put(handlerName, handler);
	}

	@Override
	public Object mediate(String handlerName, Object... args) throws P2PException {
		return this.mediationHandlers.get(handlerName).handle(args);
	}

	@Override
	public void addListener(String listenerType, IListener listener) {
		if (!this.listeners.containsKey(listenerType)) {
			this.listeners.put(listenerType, Collections.synchronizedList(new ArrayList<IListener>()));
		}

		this.listeners.get(listenerType).add(listener);
	}

	private Class<?>[] getArgumentTypes(Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().getInterfaces().length > 0) {
				types[i] = args[i].getClass().getInterfaces()[0];
			} else {
				types[i] = args[i].getClass();
			}
		}
		return types;
	}

	@Override
	public void fireEvent(String listenerType, String eventName, Object... args) {
		List<IListener> listeners = this.listeners.get(listenerType);
		synchronized (listeners) {
			for (IListener listener : listeners) {
				try {
					Method m = listener.getClass().getMethod(eventName, getArgumentTypes(args));
					m.invoke(listener, args);
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Maybe deal with this error perhaps sometime later
					e.printStackTrace();
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Maybe deal with this error perhaps sometime later
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSharedResource(String name) {
		return (T) this.sharedResources.get(name);
	}

	@Override
	public <T> void addSharedResource(String name, T resource) {
		this.sharedResources.put(name, resource);
	}

}
