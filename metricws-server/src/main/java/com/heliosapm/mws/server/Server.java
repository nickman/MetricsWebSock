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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.socket.ServerSocketChannelConfig;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heliosapm.Configuration;
import com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapper;
import com.heliosapm.jmx.concurrency.PoolThreadFactory;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper.Config;
import com.heliosapm.jmx.util.helpers.JMXHelper;
import com.heliosapm.mws.server.logging.ModifiableLoggingHandler;
import com.heliosapm.mws.server.net.http.StaticContentHandler;
import com.heliosapm.mws.server.net.ws.WebSocketServerHandler;

/**
 * <p>Title: Server</p>
 * <p>Description: The main server bootstrap</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.Server</code></p>
 */

public class Server implements ChannelPipelineFactory {
	
	private static volatile Server instance = null;
	private static final Object lock = new Object();
	
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
	
	/** The server close future */
	protected ChannelFuture closeFuture = null;
	/** The server channel */
	protected Channel serverChannel = null;
	
	
	// TODO:  add these to be JMX managed. Detect changes and trigger a new logging handler delegate
	/** The logging handler name */
	protected String loggingHandlerName = "MWSServer";
	/** true to dump content in Hex to the log, false for events only */
	protected boolean loggingHandlerHexDump = false;
	/** The level of the handler logger */
	protected InternalLogLevel loggingHandlerLevel = InternalLogLevel.INFO;
	/** Indicates if the logging handler is installed */
	protected boolean loggingHandlerInstalled = true;
	/** Indicates if the logging handler is installed before (true) or after (false) the relative named handler */
	protected boolean beforeRelativeHandler = true;
	/** The name of the relative handler where the logger handler is installed */
	protected String relativeHandler = "encoder";
	/** Booting thread */
	protected Thread bootThread = null;
	/** The server socket channel config */
	protected ServerSocketChannelConfig sscConfig = null;
	
	/** Indicates if http content chunking is enabled */
	protected boolean chunkingEnabled = Configuration.HTTP_CHUNKING_ENABLED_DEFAULT;
	/** The maximum chunk size */
	protected int maxChunkSize = Configuration.HTTP_CHUNKING_MAXSIZE_DEFAULT;
	/** Indicates if websocket frame aggregation is enabled */
	protected boolean wsAggrEnabled = Configuration.WS_AGGR_ENABLED_DEFAULT;
	/** The maximum websocket frame size */
	protected int maxFrameSize = Configuration.WS_AGGR_MAXSIZE_DEFAULT;
	
	protected final StaticContentHandler staticContentHandler = new StaticContentHandler(ConfigurationHelper.getConfig().get(Configuration.HTTP_STATIC_DIR_PROP, File.class));
	

	/** The logging handler */
	protected final ModifiableLoggingHandler loggingHandler = new ModifiableLoggingHandler(
			new LoggingHandler(loggingHandlerName, loggingHandlerLevel, loggingHandlerHexDump)
	);
	
	
	public static Server getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					final Config cfg = ConfigurationHelper.getConfig();
					final int httpPort = cfg.get(Configuration.HTTP_PORT_PROP, int.class);
					final String iface = cfg.get(Configuration.HTTP_IFACE_PROP, String.class);
					instance = new Server(httpPort, iface);
				}
			}
		}
		return instance;
	}
	
	public static Server getInstance(final int port, final String bindInterface) {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new Server(port, bindInterface);
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * Bootstraps the Server
	 * @param args as follows: <ul>
	 * 	<li><b>--mws.http.port &lt;port&gt;</b>: The port the http listener will listen on</li>
	 * 	<li><b>--</b>:  </li>
	 * </ul>
	 */
	public static void main(String[] args) {
		final Config cfg = ConfigurationHelper.getConfig(args);
		final int httpPort = cfg.get(Configuration.HTTP_PORT_PROP, int.class);
		final String iface = cfg.get(Configuration.HTTP_IFACE_PROP, String.class);
		getInstance(httpPort, iface);
	}
	
	static {
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}

	/**
	 * Creates a new Server
	 * @param port The listening port
	 * @param bindInterface The listening bind interface
	 */
	private Server(final int port, final String bindInterface) {			
		this.port = port;
		this.bindInterface = bindInterface;
		inetSockAddress = new InetSocketAddress(this.bindInterface, this.port);
		chunkingEnabled = ConfigurationHelper.getConfig().get(Configuration.HTTP_CHUNKING_ENABLED_PROP, boolean.class);
		maxChunkSize = ConfigurationHelper.getConfig().get(Configuration.HTTP_CHUNKING_MAXSIZE_PROP, int.class);
		
		wsAggrEnabled = ConfigurationHelper.getConfig().get(Configuration.WS_AGGR_ENABLED_PROP, boolean.class);		
		maxFrameSize = ConfigurationHelper.getConfig().get(Configuration.WS_AGGR_MAXSIZE_PROP, int.class);
		
		
		
		bossPool = Executors.newCachedThreadPool(PoolThreadFactory.getThreadFactory("BossPool"));
		workerPool = Executors.newCachedThreadPool(PoolThreadFactory.getThreadFactory("WorkerPool"));
		JMXExecutorServiceWrapper.register(bossPool, JMXHelper.objectName(getClass().getPackage().getName() + ":pool=BossPool"));
		JMXExecutorServiceWrapper.register(workerPool, JMXHelper.objectName(getClass().getPackage().getName() + ":pool=WorkerPool"));
		channelFactory = new NioServerSocketChannelFactory(bossPool, workerPool) {
			@Override
			public ServerSocketChannel newChannel(final ChannelPipeline pipeline) {
				ServerSocketChannel ssc = super.newChannel(pipeline);	
				sscConfig = ssc.getConfig();
				return ssc;
			}			
		};
		bootstrap = new ServerBootstrap(channelFactory);
		bootstrap.setPipelineFactory(this);
		final CountDownLatch latch = new CountDownLatch(1);
		final Throwable[] thrown = new Throwable[1];
		bootstrap.bindAsync(inetSockAddress).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture f) throws Exception {
				if(f.isSuccess()) {
					serverChannel = f.getChannel();
					closeFuture = serverChannel.getCloseFuture();					
				} else {
					thrown[0] = f.getCause();
				}
				latch.countDown();
			}
		});
		try {
			if(!latch.await(5, TimeUnit.SECONDS)) {
				throw new Exception("Server failed to start in time", new Throwable());
			}
			if(thrown[0]!=null) {
				throw new Exception("Server failed to start", thrown[0]);
			}
			LOG.info("Server started on [{}]", inetSockAddress);
		} catch (Exception ex) {
			LOG.error("Error occured while waiting for server bind.  EXITING....", ex);
			try { channelFactory.shutdown(); } catch (Exception x) {/* No Op */}
			System.exit(-1);
		}
		bootThread = Thread.currentThread();
		Thread sdt = new Thread("ServerShutdownHook"){
			@Override
			public void run() {
				shutdown();
			}
		};
		sdt.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(sdt);
		final Thread csdl = new Thread("ConsoleShutDownListener") {
			@Override
			public void run() {
				final InputStreamReader isr = new InputStreamReader(System.in, Charset.defaultCharset());
				final BufferedReader br = new BufferedReader(isr);
				String line = "";
				while(true) {
					try {
						line = br.readLine();
						if("exit".equals(line)) {
							break;
						}					
					} catch (Exception x) {/* No Op */}					
				}
				LOG.info("Console shutdown triggered");
				shutdown();
				return;
			}
		};
		csdl.setDaemon(true);
		csdl.start();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException iex) {
			LOG.info("Boot thread interrupted. Shutting down....");
			bootThread = null;
			shutdown();
		}		
	}
	
	/**
	 *  Stops the server 
	 */
	public void shutdown() {
		if(bootThread!=null) bootThread.interrupt();
		try { channelFactory.shutdown(); } catch (Exception x) {/* No Op */}
		LOG.info("Exiting.....");
	}

	/**
	 * {@inheritDoc}
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        
        if(chunkingEnabled) {
        	pipeline.addLast("httpaggregator", new HttpChunkAggregator(maxChunkSize));
        }
        
        // need WebSockHandler here.
        
        if(wsAggrEnabled) {
        	pipeline.addLast("wsaggregator", new WebSocketFrameAggregator(maxFrameSize));
        }
        pipeline.addLast("encoder", new HttpResponseEncoder());
        if(loggingHandlerInstalled) {
        	if(beforeRelativeHandler) {
        		pipeline.addBefore(relativeHandler, "logger", loggingHandler);
        	} else {
        		pipeline.addAfter(relativeHandler, "logger", loggingHandler);
        	}
        }
		return pipeline;
	}
	
	/**
	 * Returns 
	 * @return the loggingHandlerName
	 */
	public String getLoggingHandlerName() {
		return loggingHandlerName;
	}

	/**
	 * Sets 
	 * @param loggingHandlerName the loggingHandlerName to set
	 */
	public void setLoggingHandlerName(String loggingHandlerName) {
		this.loggingHandlerName = loggingHandlerName;
	}

	/**
	 * Returns 
	 * @return the loggingHandlerHexDump
	 */
	public boolean isLoggingHandlerHexDump() {
		return loggingHandlerHexDump;
	}

	/**
	 * Sets 
	 * @param loggingHandlerHexDump the loggingHandlerHexDump to set
	 */
	public void setLoggingHandlerHexDump(boolean loggingHandlerHexDump) {
		this.loggingHandlerHexDump = loggingHandlerHexDump;
	}

	/**
	 * Returns 
	 * @return the loggingHandlerLevel
	 */
	public InternalLogLevel getLoggingHandlerLevel() {
		return loggingHandlerLevel;
	}

	/**
	 * Sets the logging handler level  
	 * @param level the Logging Handler Level name to set
	 */
	public void setLoggingHandlerLevel(final String level) {
		InternalLogLevel lev = InternalLogLevel.valueOf(level.trim().toUpperCase()); 
		this.loggingHandlerLevel = lev;
	}

	/**
	 * @return
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#getBacklog()
	 */
	public int getBacklog() {
		return sscConfig.getBacklog();
	}

	/**
	 * @param backlog
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#setBacklog(int)
	 */
	public void setBacklog(int backlog) {
		sscConfig.setBacklog(backlog);
	}

	/**
	 * @return
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#isReuseAddress()
	 */
	public boolean isReuseAddress() {
		return sscConfig.isReuseAddress();
	}

	/**
	 * @param reuseAddress
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean reuseAddress) {
		sscConfig.setReuseAddress(reuseAddress);
	}

	/**
	 * @return
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() {
		return sscConfig.getReceiveBufferSize();
	}

	/**
	 * @param receiveBufferSize
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#setReceiveBufferSize(int)
	 */
	public void setReceiveBufferSize(int receiveBufferSize) {
		sscConfig.setReceiveBufferSize(receiveBufferSize);
	}

	/**
	 * @param connectionTime
	 * @param latency
	 * @param bandwidth
	 * @see org.jboss.netty.channel.socket.ServerSocketChannelConfig#setPerformancePreferences(int, int, int)
	 */
	public void setPerformancePreferences(int connectionTime, int latency,
			int bandwidth) {
		sscConfig.setPerformancePreferences(connectionTime, latency, bandwidth);
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 * @see org.jboss.netty.channel.ChannelConfig#setOption(java.lang.String, java.lang.Object)
	 */
	public boolean setOption(String name, Object value) {
		return sscConfig.setOption(name, value);
	}

	/**
	 * @return
	 * @see org.jboss.netty.channel.ChannelConfig#getConnectTimeoutMillis()
	 */
	public int getConnectTimeoutMillis() {
		return sscConfig.getConnectTimeoutMillis();
	}

	/**
	 * @param connectTimeoutMillis
	 * @see org.jboss.netty.channel.ChannelConfig#setConnectTimeoutMillis(int)
	 */
	public void setConnectTimeoutMillis(int connectTimeoutMillis) {
		sscConfig.setConnectTimeoutMillis(connectTimeoutMillis);
	}
	

}
