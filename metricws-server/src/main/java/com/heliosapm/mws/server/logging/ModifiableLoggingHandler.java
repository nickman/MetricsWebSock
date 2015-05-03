/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.mws.server.logging;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

/**
 * <p>Title: ModifiableLoggingHandler</p>
 * <p>Description: A replaceable internal logger logging handler</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.logging.ModifiableLoggingHandler</code></p>
 */

public class ModifiableLoggingHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {
	/** The delegating handler */
	protected LoggingHandler handler = null;
	
	/**
	 * Creates a new ModifiableLoggingHandler with a null initial delegating handler
	 */
	public ModifiableLoggingHandler() {}
	
	/**
	 * Creates a new ModifiableLoggingHandler
	 * @param handler The initial handler to delegate to
	 */
	public ModifiableLoggingHandler(final LoggingHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * Creates and installs a new delegating logging handler
	 * @param loggingHandlerName The logging handler name
	 * @param loggingHandlerHexDump true to dump content in Hex to the log
	 * @param loggingHandlerLevel The level of the logger
	 */
	public void update(final String loggingHandlerName, final boolean loggingHandlerHexDump, final InternalLogLevel loggingHandlerLevel) {
		handler = new LoggingHandler(loggingHandlerName, loggingHandlerLevel, loggingHandlerHexDump);
	}

	/**
	 * Returns the current delegating handler 
	 * @return the handler
	 */
	public LoggingHandler getHandler() {
		return handler;
	}

	/**
	 * Sets a new delegating handler
	 * @param handler the handler to set
	 */
	public void setHandler(final LoggingHandler handler) {
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelDownstreamHandler#handleDownstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
		final LoggingHandler h = handler;
		if(h!=null) {
			h.handleDownstream(ctx, e);
		} else {
			ctx.sendDownstream(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
		final LoggingHandler h = handler;
		if(h!=null) {
			h.handleUpstream(ctx, e);
		} else {
			ctx.sendUpstream(e);
		}
	}

}
