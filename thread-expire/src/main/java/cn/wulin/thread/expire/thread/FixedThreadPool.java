package cn.wulin.thread.expire.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可以进行统计的固定线程池
 * @author wubo
 * @param <V> 开启子线程后的返回值类型
 */
public class FixedThreadPool<V> {
	
	/**
	 * 固定线程池创建时间
	 */
	private Date fixedThreadPoolCreateTime = new Date();
	
	/**
	 * 正在执行时间
	 * 当 任务执行数 == 最哒任务执行数,则就开始记录执行时间
	 */
	private volatile Date executeTime;
	
	/**
	 * 计数锁
	 */
	private ReentrantLock countLock = new ReentrantLock(); 
	
	/**
	 * 正在执行数量
	 */
	private volatile AtomicInteger executingNumber = new AtomicInteger(0);
	
	/**
	 * 最大执行数量
	 */
	private int maxNumber;
	
	private ExecutorService executor;
	
	/**
	 * 运行中的线程名称
	 */
	private volatile List<String> runningTheadName = new ArrayList<>();
	
	/**
	 * 指定最大执行数
	 * @param maxNumber
	 */
	public FixedThreadPool(int maxNumber) {
		super();
		this.maxNumber = maxNumber;
		this.executor = Executors.newFixedThreadPool(maxNumber);
	}
	
	/**
	 * 默认最大执行数为 cpu 的核数
	 */
	public FixedThreadPool(){
		super();
		this.maxNumber = Runtime.getRuntime().availableProcessors(); // cpu 的核数
		this.executor = Executors.newFixedThreadPool(this.maxNumber);
	}
	
   /**
	 * 任务提交类  当任务提交成功,则返回当前任务,否则返回null
	 * @param fixedThreadExecute
	 * @return 当任务提交成功,则返回当前任务,否则返回null
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> submit(final ExecuteTask<V> executeTask) throws InterruptedException, ExecutionException{
		 return submit(executeTask, null,null);
	}
	
	/**
	 * 任务提交类  当任务提交成功,则返回当前任务,否则返回null
	 * @param fixedThreadExecute
	 * @param condition 重入锁对应的条件锁,表示当 condition 不为null唤醒当前条件锁对应的挂起线程
	 * @return 当任务提交成功,则返回当前任务,否则返回null
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> submit(final ExecuteTask<V> executeTask,ReentrantLock lock,Condition condition) throws InterruptedException, ExecutionException{
		FutureTask<V> futureTask = new FutureTask<V>(new Callable<V>(){
			@Override
			public V call() throws Exception {
				//记录线程名称
				String name = Thread.currentThread().getName();
				runningTheadName.add(name);
				//执行业务代码
				V callTask = executeTask.callTask();
				//子任务统计
				subTaskCount();
				//线程运行结束,移除线程名
				runningTheadName.remove(name);
				
				//唤醒当前条件锁对应lock的挂起线程
				if(lock != null && condition != null){
					try{
						lock.lock();
						condition.signalAll();
					}finally{
						if(lock.isLocked()){
							lock.unlock();
						}
					}
				}
				return callTask;
			}
		});
		return submitAndCount(futureTask);
	}
	
	/**
	 * 提交并计数
	 * @param futureTask
	 */
	private FutureTask<V> submitAndCount(FutureTask<V> futureTask){
		try {
			countLock.lock();
			if(executingNumber.get()<maxNumber){
				executingNumber.incrementAndGet();
				
				if(executingNumber.get()==maxNumber){
					executeTime = new Date();
				}
				executor.submit(futureTask);
				return futureTask;
			}
			return null;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}
	
	/**
	 * 子任务统计
	 */
	private void subTaskCount(){
		try {
			countLock.lock();
			executingNumber.decrementAndGet();
			executeTime = null;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public int getExecutingNumber() {
		return executingNumber.get();
	}

	public int getMaxNumber() {
		return maxNumber;
	}
	
	/**
	 * 是否能提交任务
	 * @return
	 */
	public boolean isEnableSubmit(){
		try {
			countLock.lock();
			if(executingNumber.get()<maxNumber){
				return true;
			}
			return false;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public Date getFixedThreadPoolCreateTime() {
		return fixedThreadPoolCreateTime;
	}

	public List<String> getRunningTheadName() {
		return runningTheadName;
	}
}
