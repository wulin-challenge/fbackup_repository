package com.bjhy.fbackup.common.extension;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扩展点加载
 * @author wubo
 *
 */
public class ExtensionLoader {
	
	/**
	 * 实例锁
	 */
	private static Object instanceLock = new Object();
	
	/**
	 * 扩展点实例
	 */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * 得到扩展点实例
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public static<T> T getInstance(Class<T> clazz){
    	synchronized (instanceLock) {
    		Object object = EXTENSION_INSTANCES.get(clazz);
        	if(object != null){
        		return (T)object;
        	}
        	return null;
		}
    }
    
    /**
     * 得到扩展点实例(得到实现了T接口的实例)
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static<T> T getInterfaceInstance(Class<? extends T> clazz){
    	synchronized (instanceLock) {
    		Object object = EXTENSION_INSTANCES.get(clazz);
    		if(object != null){
    			return (T)object;
    		}
    		return null;
    	}
    }
    
    /**
     * 设置扩展点
     * @param clazz
     * @param instance
     */
    public static <T> void setInstance(Class<T> clazz,T instance){
    	EXTENSION_INSTANCES.putIfAbsent(clazz, instance);//该操作是原子性的,所以不需要同步
    }
    
    /**
     * 设置扩展点(设置实现了T接口的实例)
     * @param clazz
     * @param instance
     */
    public static <T> void setInterfaceInstance(Class<? extends T> clazz,T instance){
    	EXTENSION_INSTANCES.putIfAbsent(clazz, instance);//该操作是原子性的,所以不需要同步
    }
    
    /**
     * 删除实例
     * @param clazz
     */
    public static <T> void remove(Class<T> clazz){
    	synchronized (instanceLock) {
    		EXTENSION_INSTANCES.remove(clazz);
		}
    }

    
}
