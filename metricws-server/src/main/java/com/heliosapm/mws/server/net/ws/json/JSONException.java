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
 * <p>Title: JSONException</p>
 * <p>Description: JSON parsing runtime exception</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.net.ws.json.JSONException</code></p>
 */

public final class JSONException extends RuntimeException {

	  /**  */
	private static final long serialVersionUID = 912016578413581411L;

	/**
	   * Constructor.
	   * @param msg The message of the exception, potentially including a stack
	   * trace.
	   */
	  public JSONException(final String msg) {
	    super(msg);
	  }
	  
	  /**
	   * Constructor.
	   * @param cause The exception that caused this one to be thrown.
	   */
	  public JSONException(final Throwable cause) {
	    super(cause);
	  }
	  
	  /**
	   * Constructor.
	   * @param msg The message of the exception, potentially including a stack
	   * trace.
	   * @param cause The exception that caused this one to be thrown.
	   */
	  public JSONException(final String msg, final Throwable cause) {
	    super(msg, cause);
	  }
	  
}
