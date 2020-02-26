package com.atzuche.order.commons;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * json处理工具类
 * @author ruiliang.zhang
 */
public class JsonUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	/**
	 * 对象转换成JSON字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("convert obj to json error. obj="+obj, e);
		}
		return null;
	}
	
	public static <T> T fromJsonToObject(String json, Class<T> clazz) {
		if(null==json || json.trim().equals("")) {
			return null;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json.trim(), clazz);
		} catch (Exception e) {
			logger.error("convert json to object error. json="+json, e);
		}
		return null;
	}
	
	/**
	 * 将json字符串转换成List
	 * @param value
	 * @param listClass
	 * @param objClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> List<T> fromJsonToList(String value, Class<List> listClass, Class<T> objClass) {
		List<T> list = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			list = mapper.readValue(value, getCollectionType(mapper, listClass, objClass));
		} catch (Exception e) {
			logger.error("convert json to list error. value="+value, e);
		}
		return list;
	}
	
	/**
	 * 获取类型
	 * @param mapper
	 * @param collectionClass
	 * @param elementClasses
	 * @return
	 */
	private static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
	
	@SuppressWarnings("rawtypes")
	public static Map fromJsonToMap(String value){
		Map map = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			map = mapper.readValue(value, Map.class);
		} catch (Exception e) {
			logger.error("convert json to map error. value={}",value,e);
		}
		return map;
	}
	
}
