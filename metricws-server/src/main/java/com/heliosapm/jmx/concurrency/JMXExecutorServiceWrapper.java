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

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.ObjectName;

import com.heliosapm.jmx.util.helpers.JMXHelper;

/**
 * <p>Title: JMXExecutorServiceWrapper</p>
 * <p>Description: Wraps an existing thread pool executor to provide JMX support</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapper</code></p>
 */

public class JMXExecutorServiceWrapper implements JMXExecutorServiceWrapperMBean {
	/** The instrumented pool */
	protected final ThreadPoolExecutor pool;
	
	/**
	 * Creates a new thread pool wrapper and registers the management interface in the helios MBeanServer
	 * @param pool An implementation of a {@link ThreadPoolExecutor}
	 * @param on The JMX ObjectName to register the management interface under
	 * @return true if the interface was created and registered, false otherwise
	 */
	public static boolean register(final Object pool, final ObjectName on) {
		if(pool==null) throw new IllegalArgumentException("The passed executor was null");
		if(on==null) throw new IllegalArgumentException("The passed ObjectName was null");
		if(!(pool instanceof ThreadPoolExecutor)) return false;
		if(JMXHelper.isRegistered(on)) return true;
		JMXExecutorServiceWrapperMBean jsw = new JMXExecutorServiceWrapper((ThreadPoolExecutor)pool);
		JMXHelper.registerMBean(jsw, on);
		return true;
	}
	
	
	/**
	 * Creates a new JMXExecutorServiceWrapper
	 * @param pool a {@link ThreadPoolExecutor} implementation
	 */
	protected JMXExecutorServiceWrapper(final ThreadPoolExecutor pool) {
		this.pool = pool;
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#shutdown()
	 */
	@Override
	public void shutdown() {
		pool.shutdown();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#shutdownNow()
	 */
	@Override
	public int shutdownNow() {
		final List<Runnable> deadTasks = pool.shutdownNow();
		return deadTasks==null ? 0 : deadTasks.size();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#isShutdown()
	 */
	@Override
	public boolean isShutdown() {
		return pool.isShutdown();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#isTerminating()
	 */
	@Override
	public boolean isTerminating() {
		return pool.isTerminating();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return pool.isTerminated();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getThreadFactory()
	 */
	@Override
	public String getThreadFactory() {
		return pool.getThreadFactory().getClass().getName();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getRejectedExecutionHandler()
	 */
	@Override
	public String getRejectedExecutionHandler() {
		final RejectedExecutionHandler rh = pool.getRejectedExecutionHandler(); 
		return rh==null ? null : rh.getClass().getName();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#setCorePoolSize(int)
	 */
	@Override
	public void setCorePoolSize(int corePoolSize) {
		pool.setCorePoolSize(corePoolSize);
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getCorePoolSize()
	 */
	@Override
	public int getCorePoolSize() {
		return pool.getCorePoolSize();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#prestartCoreThread()
	 */
	@Override
	public boolean prestartCoreThread() {
		return pool.prestartCoreThread();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#prestartAllCoreThreads()
	 */
	@Override
	public int prestartAllCoreThreads() {
		return pool.prestartAllCoreThreads();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#allowsCoreThreadTimeOut()
	 */
	@Override
	public boolean allowsCoreThreadTimeOut() {
		return pool.allowsCoreThreadTimeOut();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#setMaximumPoolSize(int)
	 */
	@Override
	public void setMaximumPoolSize(final int maximumPoolSize) {
		pool.setMaximumPoolSize(maximumPoolSize);
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getMaximumPoolSize()
	 */
	@Override
	public int getMaximumPoolSize() {
		return pool.getMaximumPoolSize();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#setKeepAliveTime(long, java.lang.String)
	 */
	@Override
	public void setKeepAliveTime(final long time, final String unit) {		
		pool.setKeepAliveTime(time, TimeUnit.valueOf(unit.trim().toUpperCase()));
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#setKeepAliveTime(long)
	 */
	@Override
	public void setKeepAliveTime(final long time) {		
		pool.setKeepAliveTime(time, TimeUnit.SECONDS);
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getKeepAliveTime(java.lang.String)
	 */
	@Override
	public long getKeepAliveTime(final String unit) {
		return pool.getKeepAliveTime(TimeUnit.valueOf(unit.trim().toUpperCase()));
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getKeepAliveTimeMs()
	 */
	@Override
	public long getKeepAliveTimeMs() {
		return pool.getKeepAliveTime(TimeUnit.MILLISECONDS);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getKeepAliveTimeSec()
	 */
	@Override
	public long getKeepAliveTimeSec() {
		return pool.getKeepAliveTime(TimeUnit.SECONDS);
	}
	

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return pool.getQueue().getClass().getName();
	}

	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getQueueSize()
	 */
	@Override
	public int getQueueSize() {
		return pool.getQueue().size();
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getQueueCapacity()
	 */
	@Override
	public int getQueueCapacity() {
		return pool.getQueue().remainingCapacity();
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#purge()
	 */
	@Override
	public void purge() {
		pool.purge();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getPoolSize()
	 */
	@Override
	public int getPoolSize() {
		return pool.getPoolSize();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getActiveCount()
	 */
	@Override
	public int getActiveCount() {
		return pool.getActiveCount();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getLargestPoolSize()
	 */
	@Override
	public int getLargestPoolSize() {
		return pool.getLargestPoolSize();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getTaskCount()
	 */
	@Override
	public long getTaskCount() {
		return pool.getTaskCount();
	}


	/**
	 * {@inheritDoc}
	 * @see com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean#getCompletedTaskCount()
	 */
	@Override
	public long getCompletedTaskCount() {
		return pool.getCompletedTaskCount();
	}
}
