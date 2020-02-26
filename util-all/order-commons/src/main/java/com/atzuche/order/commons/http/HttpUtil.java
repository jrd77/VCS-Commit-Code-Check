package com.atzuche.order.commons.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Created by wangcheng on 2014/7/29.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String PUT = "PUT";

    private static final String ENCODEING = "UTF-8";

//    private static final String BASE_URL = "http://localhost:8080/";
    //private static final String BASE_URL = "http://10.0.3.240:7064/";
//    private static final String BASE_URL = "http://10.0.3.205:7064/";
    //10.0.3.250
//    private static final String BASE_URL = "http://10.0.3.250:7064/";
    //10.0.3.207
//    private static final String BASE_URL = "http://10.0.3.207:7064/";
    //
//    private static final String BASE_URL = "http://114.55.11.127:7064/";
    	
    
//    private static final String BASE_URL = "http://10.0.3.87:8088/app-server/";
//    private static final String BASE_URL = "http://10.0.3.213:7064/";
//    private static final String BASE_URL = "http://10.0.4.65:8080/";
    
//    private static final String BASE_URL = "http://10.0.3.223:7064/";   //http://10.0.3.223:7064/
    
//    private static final String BASE_URL = "http://10.0.3.250:7064/";
    
//    private static final String BASE_URL = "http://10.0.3.60:8081/app-server/";
//    private static final String BASE_URL = "http://10.0.3.60:8083/app-server/";
    
//    private static final String BASE_URL = "http://10.0.4.73:8086/app-server/";
//    private static final String BASE_URL = "http://10.0.3.60:8088/app-server/";
//      private static final String BASE_URL = "http://10.0.3.207:7064/";
    
//    private static final String BASE_URL = "http://10.0.3.250:7064/";
//    private static final String BASE_URL = "http://10.0.3.60:8086/app-server/";
    
//    private static final String BASE_URL = "http://114.55.235.185:7064/";  //test5
    //fee
//    private static final String BASE_URL = "http://10.0.3.87:8182/";
    //local appserver  http://localhost:7064/app-server/
    private static final String BASE_URL = "";//http://localhost:7777/
//    http://114.55.235.185:7064/
    	
//    test6
//    10.0.3.250
//    private static final String BASE_URL = "http://10.0.3.250:7064/";  //10.0.3.250
    
//     private static final String BASE_URL = "http://10.0.3.60:8080/";
    
//    /114.55.11.127
//      private static final String BASE_URL = "http://114.55.11.127:7064/";
//     private static final String BASE_URL = "http://10.0.3.60:8091/";
//    private static final String BASE_URL = "http://10.0.3.60:7064/app-server/";
    
//    private static final String BASE_URL = "http://10.0.3.207:7064/";  //test3环境
    
//    private static final String BASE_URL = "http://114.55.235.185:7064/";//"http://10.0.3.60:8086/app-server/";
    
//    private static final String BASE_URL = "http://10.0.3.207:7064/";
    
//    private static final String BASE_URL = "http://apps.atzuche.com:7064/";
    
    public static HttpResult postNew(String reqUrl, Map<String, String> reqMap) throws Exception{
        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        logger.info("reqContent="+reqContent);
        logger.info("reqUrl="+reqUrl);
        String result = send(reqUrl, reqContent, POST);
        logger.info("srv return result={}"+result);
        return om.readValue(result, HttpResult.class);
    }
    
    public static HttpResult post(String reqUrl, Map<String, Object> reqMap) throws Exception{
        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        System.out.println("reqContent="+reqContent);
        System.out.println("reqUrl="+reqUrl);
        String result = send(reqUrl, reqContent, POST);
        System.out.println("test result={}"+result);
//        String result = send(reqUrl, reqContent, PUT);
        //Gson gson = new Gson();
        //return gson.fromJson(result, HttpResult.class);
        return om.readValue(result, HttpResult.class);
    }
    public static HttpResult doPostNotGzip(String reqUrl, String jsonStr) throws Exception{
        ObjectMapper om = new ObjectMapper();
        System.out.println("reqContent="+jsonStr);
        System.out.println("reqUrl="+reqUrl);
        String result = sendNotGzip(reqUrl, jsonStr, POST);
        System.out.println("test result={}"+result);
//        String result = send(reqUrl, reqContent, PUT);
        //Gson gson = new Gson();
        //return gson.fromJson(result, HttpResult.class);
        return om.readValue(result, HttpResult.class);
    }
    
    public static HttpResult post2(String reqUrl, Map<String, Object> reqMap) throws Exception{
        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        System.out.println("reqContent="+reqContent);
        System.out.println("reqUrl="+reqUrl);
        String result = send2(reqUrl, reqContent, POST);
        System.out.println("test result={}"+result);
//        String result = send(reqUrl, reqContent, PUT);
        //Gson gson = new Gson();
        //return gson.fromJson(result, HttpResult.class);
        return om.readValue(result, HttpResult.class);
    }

    public static HttpResult get(String reqUrl) throws Exception{
        String result = send(reqUrl, "", GET);
        System.out.println(reqUrl+result);

        Gson gson = new Gson();
        ObjectMapper om = new ObjectMapper();
        return om.readValue(result, HttpResult.class);
    }

    public static HttpResult delete(String reqUrl, Map<String, Object> reqMap) throws Exception{
    	ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        String result = send(reqUrl, reqContent, DELETE);
        return om.readValue(result, HttpResult.class);
    }

    public static HttpResult put(String reqUrl, Map<String, Object> reqMap) throws Exception{
    	ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        String result = send(reqUrl, reqContent, PUT);
        return om.readValue(result, HttpResult.class);
    }

    public static HttpResult encryptPost(String reqUrl, Map<String, Object> reqMap, Object... fieldsName) throws Exception {
        System.out.println(reqUrl+":=>"+reqMap);

        StringBuffer sb = new StringBuffer();
        for (Object obj : fieldsName){
            sb.append(obj);
        }
        HttpResult httpResult = get("/serverTime");
        Map<String, Object> map = (Map<String, Object>) httpResult.getData();
        String key = (String) map.get("serverTime");
        String macSign = HMAC.encodeHmacMD5(sb.toString(), key);
        reqMap.put("MacSign", macSign);
        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        reqContent = SecurityUtils.cryption(key, reqContent);
        String result = send(reqUrl, reqContent, POST);
//        Gson gson = new Gson();
        return om.readValue(result, HttpResult.class);
    }
    
    /**
     * @author yongxin.shao
     * @param reqUrl
     * @param reqMap
     * @param fieldsName
     * @return
     * @throws Exception
     */
    public static HttpResult encryptPost1(String reqUrl, Map<String, Object> reqMap, Object... fieldsName) throws Exception {
        System.out.println(reqUrl+":=>"+reqMap);

        StringBuffer sb = new StringBuffer();
        for (Object obj : fieldsName){
            sb.append(obj);
        }
        HttpResult httpResult = get("/serverTime");
        Map<String, Object> map = (Map<String, Object>) httpResult.getData();
        String key = (String) map.get("serverTime");
        String macSign = HMAC.encodeHmacMD5(sb.toString(), key);
        reqMap.put("MacSign", macSign);
        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(reqMap);
        reqContent = SecurityUtils.cryption(key, reqContent);
        String result = null;

        //发送请求
        HttpURLConnection conn = null;
        GZIPOutputStream out = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);//是否自动处理重定向
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_web");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//传递参数使用 &链接的表单提交方式
            conn.connect();
            out = new GZIPOutputStream(conn.getOutputStream());
            //发送请求数据
            out.write(reqContent.getBytes(ENCODEING));
            out.flush();
            out.close();
            //接收返回数据
            int resCode = conn.getResponseCode();
            if(resCode == 200){
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), ENCODEING));
                result = reader.readLine();
            }else{
                System.out.println("服务器返回码："+resCode);
            }
        }catch (Exception e) {
            throw e;
        } finally{
            if(reader != null){
                reader.close();
            }
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            if(conn != null){
                conn.disconnect();
            }
        }
        return om.readValue(result, HttpResult.class);
    }

    private static String send(String reqUrl, String reqContent, String method) throws Exception {
        String resContent = null;

        HttpURLConnection conn = null;
        GZIPOutputStream out = null;
        GZIPInputStream in = null;
        BufferedReader reader = null;
        try {
        	System.out.println("BASE_URL + reqUrl="+BASE_URL + reqUrl);
            URL url = new URL(BASE_URL + reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(100000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);//是否自动处理重定向
            conn.setRequestMethod(method);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_web");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");//传递参数使用 &链接的表单提交方式
//            
//            conn.setRequestProperty("Content-Type", "application/json;version=3.0;compress=false");
            
            conn.connect();
            if (!method.equals(GET)) {
                out = new GZIPOutputStream(conn.getOutputStream());
                //发送请求数据
                out.write(reqContent.getBytes(ENCODEING));
                out.flush();
                out.close();
            }
            //接收返回数据
            int resCode = conn.getResponseCode();
            if(resCode == 200){
                in = new GZIPInputStream(conn.getInputStream());
                reader = new BufferedReader(new InputStreamReader(in, ENCODEING));
                resContent = reader.readLine();
            }else{
                System.out.println("服务器返回码："+resCode);
            }
        } catch (Exception e) {
            throw e;
        } finally{
            if(reader != null){
                reader.close();
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            if(conn != null){
                conn.disconnect();
            }
        }
        return resContent;
    }
    private static String sendNotGzip(String reqUrl, String reqContent, String method) throws Exception {
        String resContent = null;
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            System.out.println("BASE_URL + reqUrl="+BASE_URL + reqUrl);
            URL url = new URL(BASE_URL + reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(100000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);//是否自动处理重定向
            conn.setRequestMethod(method);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_web");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");//传递参数使用 &链接的表单提交方式
//
//            conn.setRequestProperty("Content-Type", "application/json;version=3.0;compress=false");

            conn.connect();
            if (!method.equals(GET)) {
                out = conn.getOutputStream();
                //发送请求数据
                out.write(reqContent.getBytes(ENCODEING));
                out.flush();
                out.close();
            }
            //接收返回数据
            int resCode = conn.getResponseCode();
            if(resCode == 200){
                in = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, ENCODEING));
                resContent = reader.readLine();
            }else{
                System.out.println("服务器返回码："+resCode);
            }
        } catch (Exception e) {
            throw e;
        } finally{
            if(reader != null){
                reader.close();
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return resContent;
    }
    
    private static String send2(String reqUrl, String reqContent, String method) throws Exception {
        String resContent = null;

        HttpURLConnection conn = null;
//        GZIPOutputStream out = null;
        OutputStream out = null;
//        GZIPInputStream in = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(100000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);//是否自动处理重定向
            conn.setRequestMethod(method);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_web");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");//传递参数使用 &链接的表单提交方式
            conn.connect();
            if (!method.equals(GET)) {
//                out = new GZIPOutputStream(conn.getOutputStream());
            	out = conn.getOutputStream();
                //发送请求数据
                out.write(reqContent.getBytes(ENCODEING));
                out.flush();
                out.close();
            }
            //接收返回数据
            int resCode = conn.getResponseCode();
            if(resCode == 200){
//                in = new GZIPInputStream(conn.getInputStream());
            	in = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, ENCODEING));
                resContent = reader.readLine();
            }else{
                System.out.println("服务器返回码："+resCode);
            }
        } catch (Exception e) {
            throw e;
        } finally{
            if(reader != null){
                reader.close();
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            if(conn != null){
                conn.disconnect();
            }
        }
        return resContent;
    }

    private static InputStream sendImg(String reqUrl, String reqContent, String method) throws Exception {
        String resContent = null;

        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(BASE_URL + reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(8000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);//是否自动处理重定向
            conn.setRequestMethod(method);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_web");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//传递参数使用 &链接的表单提交方式
            conn.connect();
            //接收返回数据
            int resCode = conn.getResponseCode();
            if(resCode == 200){
                in = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, ENCODEING));
                resContent = reader.readLine();
            }else{
                System.out.println("服务器返回码："+resCode);
            }
        } catch (Exception e) {
            throw e;
        } finally{
            if(reader != null){
                reader.close();
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            if(conn != null){
                conn.disconnect();
            }
        }
        return in;
    }
    
    public static void main(String[] args) throws Exception {
       //HttpResult httpResult = get("/car/v4/list?lon=0.000000&lat=0.000000&gbType=0&carType=0&seq=0&pageNum=1&pageSize=10&token=0&startTime=0&endTime=0&brandId=0");
        HttpResult httpResult = encryptPost("/mem/login", new HashMap(){{put("loginName", "15221598362");put("pwd", "1989123");}}, "15221598362", "1989123");
        System.out.print(httpResult.getResMsg());
    }
    
    /**
     * 参数拼接
     * @param body
     * @return
     */
    public static String getPamStr(Map<String, Object> body) {
		String urel="?";
		Set<String> key=body.keySet();
		for (String string : key) {
			urel+=string+"="+body.get(string).toString()+"&";
		}
		urel=	urel.substring(0,urel.length()-1);
		return urel;
	}
    
}
