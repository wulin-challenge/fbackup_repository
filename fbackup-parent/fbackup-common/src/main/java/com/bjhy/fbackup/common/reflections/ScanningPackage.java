package com.bjhy.fbackup.common.reflections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

/**
 * 扫包
 * @author wubo
 *
 */
public class ScanningPackage {
	
	/**
	 * 通过包名和注解Class得到对应的class集合
	 * @param packagePath
	 * @return
	 */
	public static List<Class<?>> findClassByPackageAndAnnotation(String packagePath,Class<? extends Annotation> annotation){
		Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(annotation);
		return new ArrayList<Class<?>>(typesAnnotatedWith);
	}

}
