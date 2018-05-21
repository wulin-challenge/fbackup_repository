package cn.wulin.thread.expire.thread;

/**
 * 执行任务
 * @author wubo
 *
 */
public interface ExecuteTask<V> {

	/**
	 * 调用任务
	 * @return
	 */
	public V callTask() throws InterruptedException;
}
