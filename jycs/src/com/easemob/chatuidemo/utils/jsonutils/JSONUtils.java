/**  
* @company Finedo.cn
* @author WuFeng
* @date 2014-3-6 下午02:53:45
* @Title: JSONUtils.java
* @Package cn.finedo.json
* @Description: Json与java对象互转工具类
* @version V1.0  
*/ 
package com.easemob.chatuidemo.utils.jsonutils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class JSONUtils {
	
	/**
	 * @author	放火灭了雨
	 * @date 2014-3-6 下午02:58:25
	 * @Description: java对象转json字符串
	 * @param obj
	 * @return
	 */
	public static String parseJson(Object obj){
		String json = JSON.toJSONString(obj);
		return json;
	}
	
	/**
	 * @author	放火灭了雨
	 * @date 2014-3-6 下午02:58:25
	 * @Description: json字符串转java对象，
	 * 		范型调用实例 ReturnValue<LoginInfo> ret = JSON.parseObject(json, new TypeReference<ReturnValue<LoginInfo>>(){});
	 * @param obj
	 * @return 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object parseObject(String jsonString, Class ref){
		Object ret = JSON.parseObject(jsonString, ref);
		return ret;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object parseObject(String jsonString, TypeReference ref){
		Object ret = JSON.parseObject(jsonString, ref);
		return ret;
	}
}
