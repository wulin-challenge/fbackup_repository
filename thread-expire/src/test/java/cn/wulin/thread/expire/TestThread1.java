package cn.wulin.thread.expire;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThread1 {
	ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private BlockingQueue<FutureTask<String>> fixedQueue= new LinkedBlockingQueue<FutureTask<String>>(1);
	
	/**
	 * 定时清理过期线程
	 */
	private final ScheduledThreadPoolExecutor timedCleanExpiredThread = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
	
	public static void main(String[] args) throws InterruptedException {
		TestThread1 testThread1 = new TestThread1();
		testThread1.test2();
		for (int i = 0; i < 10; i++) {
			testThread1.test1();
		}
	}
	
	public void test1() throws InterruptedException{
		
		FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>(){
			@Override
			public String call() throws Exception {
				System.out.println(11);
				System.out.println(22);
				System.out.println(33);
				return null;
			}
		});
		executor.submit(futureTask);
		fixedQueue.put(futureTask);
	}
	
	public void test2(){
		timedCleanExpiredThread.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Iterator<FutureTask<String>> iterator = fixedQueue.iterator();
				while(iterator.hasNext()){
					FutureTask<String> next = iterator.next();
					next.cancel(true);
				}
			}
			
		}, 1, 5, TimeUnit.SECONDS);
	}
	
	

}
