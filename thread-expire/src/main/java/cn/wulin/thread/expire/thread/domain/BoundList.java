package cn.wulin.thread.expire.thread.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有界list集合
 * 集合最末的那个元素是存活时间最久的那一个
 * @author wubo
 *
 */
public class BoundList<V> {
	
	/**
	 * 盛装元素的list集合
	 */
	private List<V> list = new ArrayList<V>();
	
	/**
	 * 统计锁
	 */
	private ReentrantLock countLock = new ReentrantLock();
	
	/**
	 * 最大长度
	 */
	private int maxSize;
	
	public BoundList(int maxSize){
		this.maxSize = maxSize;
	}
	
	/**
	 * 添加元素,添加成功就返回true,否则就失败
	 * @param v 元素
	 * @return 添加成功就返回true,否则就失败
	 */
	public boolean add(V v){
		try {
			countLock.lock();
			if(list.size()<maxSize){
				list.add(0,v);
				return true;
			}
			return false;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}
	
	/**
	 * 通过元素索引获取元素,如果获取成功则返回为元素,否则返回null
	 * @param i 元素索引
	 * @return 如果获取成功则返回为元素,否则返回null
	 */
	public V get(int i){
		try {
			countLock.lock();
			if(i<list.size()){
				return list.get(i);
			}
			return null;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}
	
	/**
	 * 获取第一个元素:如果元素集合中没有元素则返回空
	 * @return 如果元素集合中没有元素则返回空
	 */
	public V getFirstElement(){
		try {
			countLock.lock();
			return get(0);
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}
	
	/**
	 * 获取最后一个元素:如果元素集合中没有元素则返回空
	 * @return 如果元素集合中没有元素则返回空
	 */
	public V getLastElement(){
		try {
			countLock.lock();
			int index = list.size()-1;
			V v = get(index);
			list.remove(index);
			return v;
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}
	
	/**
	 * 得到元素长度
	 * @return
	 */
	public int size(){
		try {
			countLock.lock();
			return list.size();
		} finally {
			if(countLock.isLocked()){
				countLock.unlock();
			}
		}
	}

	public int getMaxSize() {
		return maxSize;
	}
}
