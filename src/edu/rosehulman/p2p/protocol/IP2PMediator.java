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

package edu.rosehulman.p2p.protocol;

public interface IP2PMediator {

	public IHost getLocalHost();

	public String getRootDirectory();

	public void setConnected(IHost host, IStreamMonitor monitor);

	public IPacket getRequest(int number);

	public void logRequest(int number, IPacket p);

	public void completeRequest(int number);

	public void addMediationHandler(String handlerName, IMediationHandler handler);

	public Object mediate(String handlerName, Object... args) throws P2PException;

	public void addListener(String listenerType, IListener listener);

	public void fireEvent(String listenerType, String eventName, Object... args);

	public <T> T getSharedResource(String name);

	public <T> void addSharedResource(String name, T resource);
}
