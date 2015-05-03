package com.heliosapm.jmx.concurrency;

/**
 * <p>Title: JMXExecutorServiceWrapperMBean</p>
 * <p>Description: JMX JMBean interface for {@link JMXExecutorServiceWrapper}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmx.concurrency.JMXExecutorServiceWrapperMBean</code></p>
 */
public interface JMXExecutorServiceWrapperMBean {

	/**
	 * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
	 * @see java.util.concurrent.ThreadPoolExecutor#shutdown()
	 */
	public abstract void shutdown();

	/**
	 * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, 
	 * and returns the number of tasks that were awaiting execution.
	 * @return the number of tasks that were awaiting execution
	 * @see java.util.concurrent.ThreadPoolExecutor#shutdownNow()
	 */
	public abstract int shutdownNow();

	/**
	 * Indicates if the pool is shut down
	 * @return true if shutdown, false otherwise
	 * @see java.util.concurrent.ThreadPoolExecutor#isShutdown()
	 */
	public abstract boolean isShutdown();

	/**
	 * Indicates if the pool is terminating
	 * @return true if terminating, false otherwise
	 * @see java.util.concurrent.ThreadPoolExecutor#isTerminating()
	 */
	public abstract boolean isTerminating();

	/**
	 * Indicates if the pool is terminated
	 * @return true if terminated, false otherwise
	 * @see java.util.concurrent.ThreadPoolExecutor#isTerminated()
	 */
	public abstract boolean isTerminated();

	/**
	 * Returns the class name of the thread factory
	 * @return the class name of the thread factory
	 * @see java.util.concurrent.ThreadPoolExecutor#getThreadFactory()
	 */
	public abstract String getThreadFactory();

	/**
	 * Returns the class name of the RejectedExecutionHandler
	 * @return the class name of the RejectedExecutionHandler
	 * @see java.util.concurrent.ThreadPoolExecutor#getRejectedExecutionHandler()
	 */
	public abstract String getRejectedExecutionHandler();

	/**
	 * Sets the core pool size
	 * @param corePoolSize the core pool size to set
	 * @see java.util.concurrent.ThreadPoolExecutor#setCorePoolSize(int)
	 */
	public abstract void setCorePoolSize(int corePoolSize);

	/**
	 * Returns the core pool size
	 * @return the core pool size
	 * @see java.util.concurrent.ThreadPoolExecutor#getCorePoolSize()
	 */
	public abstract int getCorePoolSize();

	/**
	 * Prestarts a core thread
	 * @return true if a thread was started, false otherwise
	 * @see java.util.concurrent.ThreadPoolExecutor#prestartCoreThread()
	 */
	public abstract boolean prestartCoreThread();

	/**
	 * Prestarts all core threads
	 * @return the number of threads started
	 * @see java.util.concurrent.ThreadPoolExecutor#prestartAllCoreThreads()
	 */
	public abstract int prestartAllCoreThreads();

	/**
	 * Indicates if core threads will time out
	 * @return true if core threads will time out, false otherwise
	 * @see java.util.concurrent.ThreadPoolExecutor#allowsCoreThreadTimeOut()
	 */
	public abstract boolean allowsCoreThreadTimeOut();

	/**
	 * Sets the maximum pool size
	 * @param maximumPoolSize the maximum pool size to set
	 * @see java.util.concurrent.ThreadPoolExecutor#setMaximumPoolSize(int)
	 */
	public abstract void setMaximumPoolSize(int maximumPoolSize);

	/**
	 * Returns the maximum pool size
	 * @return the maximum pool size
	 * @see java.util.concurrent.ThreadPoolExecutor#getMaximumPoolSize()
	 */
	public abstract int getMaximumPoolSize();

	/**
	 * Sets the time limit for which threads may remain idle before being terminated.
	 * @param time the time to wait. A time value of zero will cause excess threads to terminate immediately after executing tasks.
	 * @param unit the name of the time unit of the time argument 
	 * @see java.util.concurrent.ThreadPoolExecutor#setKeepAliveTime(long, java.util.concurrent.TimeUnit)
	 */
	public abstract void setKeepAliveTime(long time, String unit);

	/**
	 * Sets the time limit for which threads may remain idle before being terminated in seconds
	 * @param time the time to wait in seconds. A time value of zero will cause excess threads to terminate immediately after executing tasks.
	 * @see java.util.concurrent.ThreadPoolExecutor#setKeepAliveTime(long, java.util.concurrent.TimeUnit)
	 */
	public abstract void setKeepAliveTime(long time);

	/**
	 * Returns the thread keep-alive time, which is the amount of time that threads in excess of the core pool size may remain idle before being terminate.
	 * @param unit the name of the desired time unit of the result 
	 * @return the time limit
	 * @see java.util.concurrent.ThreadPoolExecutor#getKeepAliveTime(java.util.concurrent.TimeUnit)
	 */
	public abstract long getKeepAliveTime(String unit);

	/**
	 * Returns the thread keep-alive time in millis
	 * @return the time limit in millis
	 * @see java.util.concurrent.ThreadPoolExecutor#getKeepAliveTime(java.util.concurrent.TimeUnit)
	 */
	public abstract long getKeepAliveTimeMs();

	/**
	 * Returns the thread keep-alive time in secs
	 * @return the time limit in secs
	 * @see java.util.concurrent.ThreadPoolExecutor#getKeepAliveTime(java.util.concurrent.TimeUnit)
	 */
	public abstract long getKeepAliveTimeSec();

	/**
	 * Returns the class name of the pool's blocking queue
	 * @return the class name of the pool's blocking queue
	 * @see java.util.concurrent.ThreadPoolExecutor#getQueue()
	 */
	public abstract String getQueueName();

	/**
	 * Returns the size of the pool's blocking queue
	 * @return the size of the pool's blocking queue
	 * @see java.util.concurrent.ThreadPoolExecutor#getQueue()
	 */
	public abstract int getQueueSize();

	/**
	 * Returns the remaining capacity of the pool's blocking queue
	 * @return the remaining capacity of the pool's blocking queue
	 * @see java.util.concurrent.ThreadPoolExecutor#getQueue()
	 */
	public abstract int getQueueCapacity();

	/**
	 * Tries to remove from the work queue all Future tasks that have been cancelled.
	 * @see java.util.concurrent.ThreadPoolExecutor#purge()
	 */
	public abstract void purge();

	/**
	 * Returns the current pool size
	 * @return the current pool size
	 * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
	 */
	public abstract int getPoolSize();

	/**
	 * Returns the approximate number of threads that are actively executing tasks.
	 * @return the approximate number of threads that are actively executing tasks.
	 * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
	 */
	public abstract int getActiveCount();

	/**
	 * Returns the largest number of threads that have ever simultaneously been in the pool.
	 * @return the largest number of threads that have ever simultaneously been in the pool.
	 * @see java.util.concurrent.ThreadPoolExecutor#getLargestPoolSize()
	 */
	public abstract int getLargestPoolSize();

	/**
	 * Returns the approximate total number of tasks that have ever been scheduled for execution.
	 * @return the approximate total number of tasks that have ever been scheduled for execution.
	 * @see java.util.concurrent.ThreadPoolExecutor#getTaskCount()
	 */
	public abstract long getTaskCount();

	/**
	 * Returns the approximate total number of tasks that have completed execution.
	 * @return the approximate total number of tasks that have completed execution.
	 * @see java.util.concurrent.ThreadPoolExecutor#getCompletedTaskCount()
	 */
	public abstract long getCompletedTaskCount();

}