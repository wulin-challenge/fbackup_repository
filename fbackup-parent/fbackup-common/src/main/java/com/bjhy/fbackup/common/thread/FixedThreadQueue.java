package com.bjhy.fbackup.common.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.bjhy.fbackup.common.util.FileUtil;

/**
 * 固定线程队列(即可以边加人任务边执行的线程控制类,并且还可以收集所有的返回值)
 * 该类已经被废弃,使用新的 ExpireTheadManagement 替代
 * @author wubo
 * @param <V> 返回的类型
 */
@Deprecated
public class FixedThreadQueue<V> {
	/**
	 * 线程队列列
	 */
	private BlockingQueue<String> fixedQueue;//用于控制线程数
	private ExecutorService executor;

	public FixedThreadQueue(int maxNumber) {
		super();
		this.fixedQueue= new LinkedBlockingQueue<String>(maxNumber);
		this.executor = Executors.newFixedThreadPool(maxNumber);
	}
	
	/**
	 * 任务执行类
	 * @param fixedThreadExecute
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> execute(final FixedThreadExecute<V> fixedThreadExecute) throws InterruptedException, ExecutionException{
		fixedQueue.put(FileUtil.getUUID());
		FutureTask<V> futureTask = new FutureTask<V>(new Callable<V>(){
			@Override
			public V call() throws Exception {
				V execute = fixedThreadExecute.execute();
				fixedQueue.take();
				return execute;
			}
		});
		executor.submit(futureTask);
		return futureTask;
	}
}
