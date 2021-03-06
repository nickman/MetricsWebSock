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
package com.heliosapm.mws.server.net.json.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.heliosapm.mws.server.net.json.JSONRequest;
import com.heliosapm.mws.server.net.json.RequestType;


/**
 * <p>Title: JSONRequestHandler</p>
 * <p>Description:  
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.tsdb.plugins.remoting.json.JSONRequestHandler</code></p>
 */


/**
 * <p>Title: JSONRequestHandler</p>
 * <p>Description: Annotates a named JSON data service method</p>
 * <p>  DEPRECATED: FIXME  Annotated methods must implement the signature defined in {@literal JSONDataService#processRequest(org.json.JSONObject, org.jboss.netty.channel.Channel)}.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.net.json.annotations.JSONRequestHandler</code></p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONRequestHandler {
	/**
	 * The name of the request handler which maps to the <b><code>op name</code></b> of a {@link JSONRequest}
	 */
	public String name();
	/**
	 * A description of the operation
	 */
	public String description() default "A JSON Request Operation";	

	/**
	 * The request type
	 */
	public RequestType type() default RequestType.REQUEST;
}
