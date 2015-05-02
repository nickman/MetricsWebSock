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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heliosapm.Configuration;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper;
import com.heliosapm.jmx.util.helpers.ConfigurationHelper.Config;

/**
 * <p>Title: Server</p>
 * <p>Description: The main server bootstrap</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.Server</code></p>
 */

public class Server {
	
	/** Static class logger */
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	
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
		LOG.info("HTTP Port: {}", httpPort);
	}

}
