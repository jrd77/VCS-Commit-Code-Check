package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * json转换工具
 * @author zhiping.li
 * @date 2016-03-29
 * @version 1.0
 */
public class JsonUtils {

	private static ObjectMapper mapper = new ObjectMapper();
	/**
	 * json处理时，去掉空字段
	 * @param object
	 */
	public static JSONObject discardNullField(Object object)
	{
		
		JSONObject json = JSONObject.fromObject(object);
		JSONObject newJson = new JSONObject();
		Iterator iterator = json.keys();  
		 while (iterator.hasNext()) {  
             String key = (String) iterator.next();
             Object value = json.get(key);
             String strValue = json.getString(key);
             if(strValue!=null&&!strValue.equals("null")&&!strValue.equals("")&&!strValue.equals("\"null\""))
             {
            	 int type = checkType(value);
                 //处理数组类型
                 if(type==2)
                 {
                	 newJson.put(key, handleArray(JSONArray.fromObject(value)));
                	 
                 }//处理JSON对象类型
                 else if(type==1)
                 {
                	 newJson.put(key, discardNullField(value));
                 }//对普通值处理
                 else
                 {
                	 newJson.put(key, value);
                 }
             }
		 }
		 return newJson;
	}
	
	
	
	
	/**
	 * 判断json对象字符串的类型
	 * @param
	 * @return
	 */
	public static int checkType(Object jsonObj)
	{
		/*
		 * 0:既不是array也不是object(非法json格式) 1：是object 2 ：是Array
		 */
		if (jsonObj instanceof JSONArray)
		{
			return 2;
		}
		else if (jsonObj instanceof JSONObject)
		{
			return 1;
		}
		else
		{
			return 0;
		}

	}
	
	
	/**
	 * 对数组中的空字段进行处理
	 * @param jsonArray
	 * @return
	 */
	public static JSONArray handleArray(JSONArray jsonArray)
	{
		int size = jsonArray.size();
		JSONArray newJsonArray = new JSONArray();
		for(int i=0; i< size; i++)
		{
			Object object = jsonArray.get(i);
			 int type = checkType(object);
             //处理数组类型
             if(type==2)
             {
            	 //递归调用处理数组
            	 newJsonArray.add(handleArray(JSONArray.fromObject(object)));
             }//处理JSON对象类型
             else if(type==1)
             {
            	 //递归调用json对象
            	 newJsonArray.add(discardNullField(object));
             }//数组对象不会存在type为0的情况
    
		}
		
		return newJsonArray;
		
	}
	

	static{
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}
	/**
	 * 转换对象为json字符串
	 * @param t
	 * @return
	 * @throws JsonProcessingException
	 */
	public static <T> String fromObject(T t) throws Exception{
		return mapper.writeValueAsString(t);
	}

	/**
	 * 转换json为指定类型值得map
	 * @param json
	 * @return
	 * @throws
	 */
	public static <T> Map<String, T> toMap(String json) throws Exception{
		return mapper.readValue(json, new TypeReference<HashMap<String,T>>(){});
	}

	/**
	 * 转换json字符串为指定类型的list集合
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> toList(String json,JavaType javaType) throws Exception{
		return mapper.readValue(json, javaType);
	}
	@SuppressWarnings("rawtypes")
	public static CollectionType getCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClasses) {
		return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClasses);   
	}  
	/**
	 * 转换json字符串为指定的实体类
	 * @param json
	 * @param class1
	 * @return
	 * @throws Exception
	 */
	public static <T> T toBean(String json,Class<T> class1) throws Exception{
		return mapper.readValue(json, class1);
	}
	 

}
