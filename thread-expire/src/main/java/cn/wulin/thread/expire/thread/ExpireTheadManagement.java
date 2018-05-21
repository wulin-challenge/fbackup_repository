package cn.wulin.thread.expire.thread;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import cn.wulin.thread.expire.thread.domain.BoundList;

/**
 * 过期线程管理
 * @author wubo
 *
 */
public class ExpireTheadManagement<V> {
	private CleanExpireTask<V> cleanExpireTask;
	
	/**
	 * 线程存活的生命周期
	 */
	private BoundList<FixedThreadPool<V>> theadPoolLifeCycle;
	
	/**
	 * 默认第一个元素的存活时间为 10 分钟
	 */
	private int firstElementSurvivalTime;
	
	private ReentrantLock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();
	
	/**
	 * 线程池容量(机器自身核数)
	 */
	private int threadPoolCapacity;
	
	public ExpireTheadManagement(){
		this(10);
	}
	
	/**
	 * @param maxSize 最大能容纳多少固定线程池元素
	 */
	public ExpireTheadManagement(int maxSize){
		this(maxSize, 1000*60*15);
	}
	
	/**
	 * @param maxSize 最大能容纳多少固定线程池元素
	 * @param firstElementSurvivalTime 当第一个元素的线程池满了后,最多能存活多久(单位:毫秒)
	 */
	public ExpireTheadManagement(int maxSize,int firstElementSurvivalTime){
		this(maxSize, firstElementSurvivalTime,Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * @param maxSize 最大能容纳多少固定线程池元素
	 * @param firstElementSurvivalTime 当第一个元素的线程池满了后,最多能存活多久(单位:毫秒)
	 * @param threadPoolCapacity 线程池的容量
	 */
	public ExpireTheadManagement(int maxSize,int firstElementSurvivalTime,int threadPoolCapacity){
		this.theadPoolLifeCycle = new BoundList<FixedThreadPool<V>>(maxSize);
		this.cleanExpireTask = new CleanExpireTask<V>(this);//启动过期清理
		this.threadPoolCapacity = threadPoolCapacity;
		this.firstElementSurvivalTime = firstElementSurvivalTime;
	}
	
	/**
	 * 添加一个新的执行任务,并保证一定能添加成功,若添加失败,将会被挂起,直到子线程释放或者过期线程将过期线程池元素回收
	 * @param executeTask 新的执行任务
	 * @return 返回新添加的执行任务的FutureTask对象,该对象一定不会为null
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> putNewTask(ExecuteTask<V> executeTask) throws InterruptedException, ExecutionException{
		try {
			lock.lock();
			FutureTask<V> newTask = newTask(executeTask);
			while(newTask == null){
				condition.await();
				newTask = newTask(executeTask);
			}
			return newTask;
		} finally {
			if(lock.isLocked()){
				lock.unlock();
			}
		}
	}
	
	/**
	 * 在给定的时间范围内添加一个新的执行任务:但不保证一定能添加成功,若添加成功则返回该任务的FutureTask对象,否则返回null
	 * @param executeTask 新的执行任务
	 * @param timeout 超时时间
	 * @param unit 时间单位
	 * @return 若添加成功则返回该任务的FutureTask对象,否则返回null
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> newTask(ExecuteTask<V> executeTask,long timeout,TimeUnit unit) throws InterruptedException, ExecutionException{
		try {
			lock.lock();
			FutureTask<V> newTask = newTask(executeTask);
			if(newTask == null){
				condition.await(timeout, unit);
				newTask = newTask(executeTask);
			}
			return newTask;
		} finally {
			if(lock.isLocked()){
				lock.unlock();
			}
		}
	}
	
	/**
	 * 添加一个新的执行任务:但不保证一定能添加成功,若添加成功则返回该任务的FutureTask对象,否则返回null
	 * @param executeTask 新的执行任务
	 * @return 若添加成功则返回该任务的FutureTask对象,否则返回null
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public FutureTask<V> newTask(ExecuteTask<V> executeTask) throws InterruptedException, ExecutionException{
		try {
			lock.lock();
			FixedThreadPool<V> firstElement = theadPoolLifeCycle.getFirstElement();
			if(firstElement == null){
				addNewThreadPool();
				firstElement = theadPoolLifeCycle.getFirstElement();
			}
			return newTask(executeTask, firstElement);
		} finally {
			if(lock.isLocked()){
				lock.unlock();
			}
		}
	}

	private FutureTask<V> newTask(ExecuteTask<V> executeTask, FixedThreadPool<V> firstElement)throws InterruptedException, ExecutionException {
		FutureTask<V> futureTask = firstElement.submit(executeTask,lock,condition);
		if(futureTask == null){
			boolean expire = isExpire(firstElement.getExecuteTime());
			if(expire){
				return dealWithThreadPool(executeTask);
			}
		}
		return futureTask;
	}
	
	/**
	 * 检测第一个元素是否过期
	 */
	void checkFirstElementIsExpire(){
		FixedThreadPool<V> firstElement = theadPoolLifeCycle.getFirstElement();
		if(firstElement != null){
			boolean expire = isExpire(firstElement.getExecuteTime());
			if(expire){
				int size = theadPoolLifeCycle.size();
				int maxSize = theadPoolLifeCycle.getMaxSize();
				if(size>=maxSize){
					cleanExpireTask.put(theadPoolLifeCycle.getLastElement());
				}
			}
		}
		try{
			lock.lock();
			condition.signalAll();
		}finally{
			if(lock.isLocked()){
				lock.unlock();
			}
		}
		
	}

	/**
	 * 处理过期线程池
	 * @param executeTask
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private FutureTask<V> dealWithThreadPool(ExecuteTask<V> executeTask)throws InterruptedException, ExecutionException {
		int size = theadPoolLifeCycle.size();
		int maxSize = theadPoolLifeCycle.getMaxSize();
		if(size>=maxSize){
			cleanExpireTask.put(theadPoolLifeCycle.getLastElement());
			try{
				lock.lock();
				condition.signalAll();
			}finally{
				if(lock.isLocked()){
					lock.unlock();
				}
			}
		}
		addNewThreadPool();
		FixedThreadPool<V> firstElement = theadPoolLifeCycle.getFirstElement();
		return newTask(executeTask, firstElement);
	}
	
	/**
	 * 添加新的线程池到新的位置
	 */
	private void addNewThreadPool(){
		FixedThreadPool<V> fixedThreadPool = new FixedThreadPool<V>(threadPoolCapacity);
		theadPoolLifeCycle.add(fixedThreadPool);
	}
	
	/**
	 * 缓存是否过期
	 * @param start 开始日期
	 * @return
	 */
	private boolean isExpire(Date start){
		if(start == null){
			return false;
		}
		Long startTime = start.getTime();
		Long currentTime = new Date().getTime();
		
		if((currentTime-startTime)>=firstElementSurvivalTime){
			return true;
		}
		return false;
	}

}
