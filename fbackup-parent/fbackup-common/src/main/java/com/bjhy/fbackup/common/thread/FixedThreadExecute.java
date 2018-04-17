package com.bjhy.fbackup.common.thread;

/**
 * 固定线程的执行回调接口
 * @author wubo
 * @param <V>
 */
public interface FixedThreadExecute<V> {
	
	public V execute();

}
