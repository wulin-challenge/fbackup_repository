package com.bjhy.fbackup.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于创建表的注解,主要是用于获取表名
 * @author wubo
 */
@Retention(RetentionPolicy.RUNTIME) //作用域到运行时
@Target({ElementType.TYPE}) //作用域到类/接口/枚举
@Documented
public @interface EntityTable {
	
	/**
	 * 表名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 是否创建表
	 * @return
	 */
	boolean isCreateTable() default true;

}
