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
package com.heliosapm.mws.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heliosapm.Configuration;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper.Config;
import com.heliosapm.mws.server.net.ws.WebSocketServerHandler;

/**
 * <p>Title: Server</p>
 * <p>Description: The main server bootstrap</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.Server</code></p>
 */

public class Server implements ChannelPipelineFactory {
	
	/** Static class logger */
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	/** The listening port */
	protected int port = Configuration.HTTP_PORT_DEFAULT;
	/** The listening bind interface */
	protected String bindInterface = Configuration.HTTP_IFACE_DEFAULT;
	/** The listener inet socket address */
	protected InetSocketAddress inetSockAddress = null;
	/** The server bootstrap */
	protected ServerBootstrap bootstrap = null;
	/** The socket channel factory */
	protected NioServerSocketChannelFactory channelFactory = null;
	/** The boss thread pool */
	protected ExecutorService bossPool = null;
	/** The worker thread pool */
	protected ExecutorService workerPool = null;
	
	/**
	 * Bootstraps the Server
	 * @param args as follows: <ul>
	 * 	<li><b>--mws.http.port &lt;port&gt;</b>: The port the http listener will listen on</li>
	 * 	<li><b>--</b>:  </li>
	 * </ul>
	 */
	public static void main(String[] args) {
		final Config cfg = ConfigurationHelper.newInstance(args);
		final int httpPort = cfg.get(Configuration.HTTP_PORT_PROP, int.class);
		final String iface = cfg.get(Configuration.HTTP_IFACE_PROP, String.class);
		LOG.info("HTTP Listening Socket: {}:{}", iface, httpPort);
	}

	/**
	 * Creates a new Server
	 * @param port The listening port
	 * @param bindInterface The listening bind interface
	 */
	private Server(final int port, final String bindInterface) {		
		this.port = port;
		this.bindInterface = bindInterface;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", new WebSocketServerHandler());		
		return pipeline;
	}
	
	

}
