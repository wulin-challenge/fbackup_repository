package com.bjhy.fbackup.common.util;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.fbackup.common.annotation.FBackupListener;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.reflections.ScanningPackage;

/**
 * 监听工具类
 * @author wubo
 */
@SuppressWarnings("unchecked")
public class ListenerUtil {
	/**
	 * 得到 T 接口的实现类
	 * @return
	 */
	public static <T> List<Class<? extends T>> getListenerClass(Class<T> listenerClass){
		List<Class<? extends T>> classList = new ArrayList<Class<? extends T>>();
		List<Class<?>> fbackupListenerList = ScanningPackage.findClassByPackageAndAnnotation(ConstantUtil.SCANNING_PACKAGE, FBackupListener.class);
		for (Class<?> clazz : fbackupListenerList) {
			Class<?>[] interfaces = clazz.getInterfaces();
			inter:for (Class<?> class1 : interfaces) {
				if(class1 == listenerClass){
					Class<? extends T> listenerClassImpl =(Class<? extends T>) clazz;
					classList.add(listenerClassImpl);
					break inter;
				}
			}
		}
		return classList;
	}
	
	/**
	 * 得到 T 接口所有实现类的所有实例
	 * @param interfaceClass T 接口所有实现类
	 * @return
	 */
	public static <T> List<T> getInterfaceInstances(List<Class<? extends T>> interfaceClass){
		List<T> interfaceInstances = new ArrayList<T>();
		for (Class<? extends T> clazz : interfaceClass) {
			T instance = ExtensionLoader.getInstance(clazz);
			if(instance == null){
				try {
					instance = clazz.newInstance();
					ExtensionLoader.setInterfaceInstance(clazz, instance);
				} catch (InstantiationException e) {
					LoggerUtils.error("InstantiationException 异常", e);
				} catch (IllegalAccessException e) {
					LoggerUtils.error("IllegalAccessException 异常", e);
				}
			}
			interfaceInstances.add(instance);
		}
		return interfaceInstances;
	}
}
