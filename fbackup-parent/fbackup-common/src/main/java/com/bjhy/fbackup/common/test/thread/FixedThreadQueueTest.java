package com.bjhy.fbackup.common.test.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.bjhy.fbackup.common.domain.XmlFbackup;
import com.bjhy.fbackup.common.util.FileUtil;

/**
 * 固定线程队列
 * @author Administrator
 *
 * @param <V> 返回的类型
 */
public class FixedThreadQueueTest<V> {
	/**
	 * 线程队列列
	 */
	private BlockingQueue<String> fixedQueue;//用于控制线程数
	private ExecutorService executor;

	public FixedThreadQueueTest(int maxNumber) {
		super();
		this.fixedQueue= new LinkedBlockingQueue<String>(maxNumber);
		this.executor = Executors.newFixedThreadPool(maxNumber);
	}
	
	public FutureTask<V> execute(final FixedThreadExecuteTest<V> fixedThreadExecute) throws InterruptedException, ExecutionException{
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
	
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		FixedThreadQueueTest<String> xx = new FixedThreadQueueTest<String>(300);
		
		for (int i = 0; i < 50000000; i++) {
			final int j =i;
			FutureTask<String> execute = xx.execute(new FixedThreadExecuteTest<String>(){
				@Override
				public String execute() {
					System.out.println(j);
					return j+"";
				}
				
			});
			
//			System.out.println(execute);
		}
		
		System.out.println("-------------------");
		
		
	}
}
