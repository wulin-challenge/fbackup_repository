package com.bjhy.fbackup.common.interrupt;

import cn.wulin.ioc.extension.SPI;

/**
 * 用于额外的扩展拦截所用
 * @author wubo
 * @param <T>
 */
@SPI
public interface Interrupt<T> {
	
	void before(T entity,Class<T> clazz);
	
	void after(T entity,Class<T> clazz);

}
