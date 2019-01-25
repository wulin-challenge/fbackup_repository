package com.bjhy.fbackup.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *回调的监听器注解
 * @author wubo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface FBackupListener {
	
	/**
	 * 是否为 fbackup 项目内部实现,默认为true
	 * @return 当返回 false 时,将不再使用 fbackup的内部实现,而是使用用户自定义的实现
	 */
	boolean isFbackupInternal() default true;
}
