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
package com.heliosapm;

import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.io.File;

/**
 * <p>Title: Configuration</p>
 * <p>Description: Configuration constants and defaults</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.Configuration</code></p>
 */

public class Configuration {

	/** The HTTP listening port */
	public static final String HTTP_PORT_PROP = "mws.http.port";
	/** The default HTTP listening port */
	public static final int HTTP_PORT_DEFAULT = 8134;
	/** The HTTP listening bind interface */
	public static final String HTTP_IFACE_PROP = "mws.http.iface";
	/** The default HTTP listening bind interface */
	public static final String HTTP_IFACE_DEFAULT = "0.0.0.0";
	/** The root directory to serve static content from */
	public static final String HTTP_STATIC_DIR_PROP = "mws.http.static.dir";
	/** The default HTTP listening bind interface */
	public static final File HTTP_STATIC_DIR_DEFAULT = new File(new File(System.getProperty("user.home")), ".mws" + File.separator + "static");
	
	
	class FilePropertyEditor extends PropertyEditorSupport {
		@Override
		public String getAsText() {
			final File f = (File)getValue();			
			return f==null ? null : f.getAbsolutePath();
		}
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			setValue(new File(text.trim()));
		}
	}
	
	static {
		HTTP_STATIC_DIR_DEFAULT.mkdirs();
		PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
