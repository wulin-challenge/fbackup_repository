package com.bjhy.fbackup.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 加载 db.properties配置文件
 * @author wubo
 */
public class CenterPropUtil {

	public static final String CHAT_CONFIG_PROPERTY_LOC = "/config/center.properties";
	
	private static PropertiesConfiguration properties;
	
	static{
		try {
			properties = new PropertiesConfiguration(new File(System.getProperty("user.dir") + CHAT_CONFIG_PROPERTY_LOC));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key,boolean isRefresh){
		if(isRefresh){
			try {
				properties = new PropertiesConfiguration(new File(System.getProperty("user.dir") + CHAT_CONFIG_PROPERTY_LOC));
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		return getProperty(key);
	}
	
	
	/**
	 * 随机拿到第一个，该方法一般很少使用，除非确定当前的key值是全系统唯一的
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getProperty(String key){
		Object values = properties.getProperty(key);
		if(values != null){
			if(values instanceof List){
				List<String> valueList = (List<String>) values;
				StringBuffer valueChars = new StringBuffer();
				boolean isFirst = true;
				for (Object obj : valueList) {
					if(isFirst){
						isFirst = false;
						valueChars.append((String)obj);
					}else{
						valueChars.append(","+(String)obj);
					}
				}
				return valueChars.toString();
			}
			return (String) properties.getProperty(key);
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param defaultValue 默认值
	 * @param clazz 返回值类型
	 * @param isRefresh 是否刷新
	 * @return
	 */
	public static <T> T getProperty(String key,T defaultValue,Class<T> clazz,boolean isRefresh){
		if(isRefresh){
			try {
				properties = new PropertiesConfiguration(new File(System.getProperty("user.dir") + CHAT_CONFIG_PROPERTY_LOC));
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return getProperty(key,defaultValue,clazz);
	}
	
	/**
	 * 
	 * @param key 
	 * @param defaultValue 默认值
	 * @param clazz 返回值类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getProperty(String key,T defaultValue,Class<T> clazz){
		if(clazz == boolean.class || clazz == Boolean.class){
			boolean value = properties.getBoolean(key, (boolean) defaultValue);
			Object val = value;
			return (T) val;
		}
		
		if(clazz == Integer.class || clazz == int.class){
			Integer value = properties.getInteger(key, (Integer) defaultValue);
			Object val = value;
			return (T) val;
		}
		
		if(clazz == Long.class || clazz == long.class){
			Long value = properties.getLong(key, (Long) defaultValue);
			Object val = value;
			return (T) val;
		}
		
		if(clazz == Short.class || clazz == short.class){
			Short value = properties.getShort(key, (Short) defaultValue);
			Object val = value;
			return (T) val;
		}
		
		if(clazz == String.class){
			String value = properties.getString(key, (String) defaultValue);
			Object val = value;
			return (T) val;
		}
		return null;
	}
	
	/**
	 * 随机拿到第一个，该方法一般很少使用，除非确定当前的key值是全系统唯一的
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getPropertyList(String key){
		List<String> propertyValues = new ArrayList<String>();
		Object values = properties.getProperty(key);
		if(values != null){
			if(values instanceof List){
				List<String> valueList = (List<String>) values;
				propertyValues.addAll(valueList);
			}else{
				propertyValues.add((String)values);
			}
		}
		return propertyValues;
	}
	
	/**
	 * 随机设置到第一个，该方法一般很少使用，除非确定当前的key值是全系统唯一的
	 * @param key
	 * @param value
	 * @return 返回true表示设置成功,false则反之
	 */
	public static Boolean setProperty(String key,String value){
		Boolean flag = false;
		try {
			if(properties.getProperty(key) != null){
				properties.setProperty(key, value);
				properties.save();
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return flag;
	}
}

