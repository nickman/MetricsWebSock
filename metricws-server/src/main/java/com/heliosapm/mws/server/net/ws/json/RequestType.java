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
package com.heliosapm.mws.server.net.ws.json;

/**
 * <p>Title: RequestType</p>
 * <p>Description: Enumerates the request types that are handled by the websock service</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.net.ws.json.RequestType</code></p>
 */

public enum RequestType {
	/** Simple request response */
	REQUEST("req"),
	/** Compound request response with potentially multiple responses */
	MREQUEST("mreq"),	
	/** Initiates a subscription */
	SUBSCRIBE("sub"),
	/** Cancels a subscription */
	UNSUBSCRIBE("xsub");
	
	private RequestType(final String code) {
		this.code = code;
	}
	
	/** The request type code */
	public final String code;
}
