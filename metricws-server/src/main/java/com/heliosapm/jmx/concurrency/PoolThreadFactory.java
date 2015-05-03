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
package com.heliosapm.jmx.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: PoolThreadFactory</p>
 * <p>Description: Factory for thread factories</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmx.concurrency.PoolThreadFactory</code></p>
 */

public class PoolThreadFactory  {

	private static final Map<String, ThreadFactory> factories = new ConcurrentHashMap<String, ThreadFactory>();
	private static final Map<String, ThreadGroup> threadGroups = new ConcurrentHashMap<String, ThreadGroup>();
	
//    ThreadLocal.ThreadLocalMap threadLocals = null;
//    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

	/**
	 * Acquires the named ThreadFactory, creating it if it does not already exist
	 * @param name The thread name prefix
	 * @param closer An optional runnable to execute before the thread terminates
	 * @return the named thread factory
	 */
	public static ThreadFactory getThreadFactory(final String name, final Runnable closer) {
		return getThreadFactory(name, null, closer);
	}
	

	/**
	 * Acquires the named ThreadFactory, creating it if it does not already exist
	 * @param name The thread name prefix
	 * @return the named thread factory
	 */
	public static ThreadFactory getThreadFactory(final String name) {
		return getThreadFactory(name, null, null);
	}
	
	
	/**
	 * Acquires the named ThreadFactory, creating it if it does not already exist
	 * @param name The thread name prefix
	 * @param groupName The optional thread group name
	 * @return the named thread factory
	 */
	public static ThreadFactory getThreadFactory(final String name, final String groupName) {
		return getThreadFactory(name, groupName, null);
	}

	
	/**
	 * Acquires the named ThreadFactory, creating it if it does not already exist
	 * @param name The thread name prefix
	 * @param groupName The optional thread group name
	 * @param closer An optional runnable to execute before the thread terminates
	 * @return the named thread factory
	 */
	public static ThreadFactory getThreadFactory(final String name, final String groupName, final Runnable closer) {
		final String tgName;
		
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("The passed name was null or empty");
		final String tfName = name.trim();
		if(groupName==null || groupName.trim().isEmpty()) {
			tgName = tfName + "ThreadGroup";
		} else {
			tgName = groupName.trim();
		}
		final String key = tfName.trim() + tgName.trim();
		ThreadGroup tg = threadGroups.get(tgName);
		if(tg==null) {
			synchronized(threadGroups) {
				tg = threadGroups.get(tgName);
				if(tg==null) {
					tg = new ThreadGroup(tgName);
					threadGroups.put(tgName, tg);
				}
			}
		}
		final ThreadGroup ftg = tg;
		ThreadFactory tf = factories.get(key);
		if(tf==null) {
			synchronized(factories) {
				tf = factories.get(key);
				if(tf==null) {
					tf = new ThreadFactory() {
						final AtomicInteger serial = new AtomicInteger(0);
						@Override
						public Thread newThread(final Runnable r) {
							Thread t = new Thread(ftg, r, tfName + "#" + serial.incrementAndGet()) {
								@Override
								public void run() {
									super.run();
									if(closer!=null) {
										closer.run();
									}
								}
							};
							return t;
						}
					};
				}
			}
		}
		return tf;
	}
}
