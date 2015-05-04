/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.heliosapm.mws.server.net;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heliosapm.Configuration;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper;
import com.heliosapm.mws.server.net.http.StaticContentHandler;
import com.heliosapm.mws.server.net.json.JSONRequest;
import com.heliosapm.mws.server.net.json.JSONRequestRouter;
import com.heliosapm.mws.server.net.ws.WebSocketHandshakeHandler;

/**
 * <p>Title: RequestRouter</p>
 * <p>Description: Routes the incoming request to the correct end-point</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.net.RequestRouter</code></p>
 */

public class RequestRouter extends SimpleChannelHandler {
	/** A map of HTTP Request handlers keyed by the URI prefix */
	protected final Map<String, ChannelUpstreamHandler> httpHandlers = new NonBlockingHashMap<String, ChannelUpstreamHandler>(128);
	/** The websocket request handler */
	protected final JSONRequestRouter wsRouter = JSONRequestRouter.getInstance(); 
	
	/** Instance logger */
	protected final Logger LOG = LoggerFactory.getLogger(getClass());


	/** Counter for received telnet rpcs */
	protected final AtomicLong telnet_rpcs_received = new AtomicLong();
	/** Counter for received http rpcs */
	protected final AtomicLong http_rpcs_received = new AtomicLong();
	/** Counter for received websocket rpcs */
	protected final AtomicLong websock_rpcs_received = new AtomicLong();	
	/** Counter for rpc exceptions */
	protected final AtomicLong exceptions_caught = new AtomicLong();
	
	/** Indicates if http content chunking is enabled */
	protected boolean chunkingEnabled = Configuration.HTTP_CHUNKING_ENABLED_DEFAULT;
	/** Indicates if websocket frame aggregation is enabled */
	protected boolean wsAggrEnabled = Configuration.WS_AGGR_ENABLED_DEFAULT;


	
	
	/**
	 * Creates a new RequestRouter
	 */
	public RequestRouter() {
		chunkingEnabled = ConfigurationHelper.getConfig().get(Configuration.HTTP_CHUNKING_ENABLED_PROP, boolean.class);
		wsAggrEnabled = ConfigurationHelper.getConfig().get(Configuration.WS_AGGR_ENABLED_PROP, boolean.class);
		httpHandlers.put("s", new StaticContentHandler());
		httpHandlers.put("ws", new WebSocketHandshakeHandler());
		httpHandlers.put("favicon.ico", new StaticContentHandler());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
		final Object msg = e.getMessage();		
		if(msg instanceof HttpRequest) {
			final HttpRequest request = (HttpRequest)msg;
			handleHttpQuery(e.getChannel(), ctx, request, e);
			return;
		} else if(msg instanceof WebSocketFrame) {
			final WebSocketFrame frame = (WebSocketFrame)msg;
			final JSONRequest jsonRequest = JSONRequest.newJSONRequest(ctx.getChannel(), frame);
			wsRouter.route(jsonRequest);
			return;
		}
		super.messageReceived(ctx, e);
	}
	
	
    /**
     * Sends an error back to the client
     * @param ctx The channel handler context
     * @param status The HTTP error code
     * @param msg The error message
     */
    protected void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status, final String msg) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer("Failure: " + status + "\r\n" + msg + "\r\n", CharsetUtil.UTF_8));        
        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
	
	
	
	  /**
	   * Finds the right handler for an HTTP query and executes it.
	   * Also handles simple and pre-flight CORS requests if configured, rejecting
	   * requests that do not match a domain in the list.
	   * @param chan The channel on which the query was received.
	   * @param req The parsed HTTP request.
	   */
	  private void handleHttpQuery(final Channel chan, final ChannelHandlerContext ctx, final HttpRequest req, final ChannelEvent e) {
	    http_rpcs_received.incrementAndGet();
	    if(!chunkingEnabled && req.isChunked()) {
	    	sendError(ctx, HttpResponseStatus.BAD_REQUEST, "HTTP Chunking Not Enabled");
	    	return;
	    }
	    try {
	    	// TODO:  Add CORS handling
	        final String route = req.getUri().split("/")[0];
	        final ChannelUpstreamHandler handler = httpHandlers.get(route);
	        if(handler==null) {
	        	sendError(ctx, HttpResponseStatus.NOT_FOUND, "No handler found for [" + route + "]");
	        	return;
	        }
	        handler.handleUpstream(ctx, e);
	    } catch (Exception ex) {
	    	sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, ex.toString());
	    	exceptions_caught.incrementAndGet();
	    }
	  }

	
}
