package com.atzuche.order.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * @Author ZhangBin
 * @Date 2020/1/2 17:08
 * @Description: 
 * 
 **/
public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	private static final String ENCODEING = "UTF-8";
	
	
	public static String get(String reqUrl, String reqContent) throws Exception {
		String resContent = null;
		
		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(reqUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(false);//是否自动处理重定向
			conn.setRequestMethod("GET");
			conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_console");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//传递参数使用 &链接的表单提交方式
            conn.connect();
            out = conn.getOutputStream();
            //发送请求数据
//			out.write(reqContent.getBytes(ENCODEING));
//			out.flush();
//			out.close();
            
			//接收返回数据
			int resCode = conn.getResponseCode();
			//System.err.println("resCode:"+resCode);
			if(resCode == 200){
				in = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				resContent = reader.readLine();
			}else{
				logger.error("服务器返回码："+resCode);
			}
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		} finally{
			if(reader != null){
				reader.close();
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		return resContent;
	}
	
	
	public static String post(String reqUrl, String reqContent) throws Exception {
		String resContent = null;
		
		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(reqUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(false);//是否自动处理重定向
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "AutoyolEs_console");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");//传递参数使用 &链接的表单提交方式
            conn.connect();
            out = conn.getOutputStream();
            //发送请求数据
			out.write(reqContent.getBytes(ENCODEING));
			out.flush();
			out.close();
			//接收返回数据
			int resCode = conn.getResponseCode();
			//System.err.println("resCode:"+resCode);
			if(resCode == 200){
				in = conn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				resContent = reader.readLine();
			}else{
				logger.error("服务器返回码："+resCode);
			}
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		} finally{
			if(reader != null){
				reader.close();
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		return resContent;
	}

	/*public static void main(String[] args) {
		
	}*/
	
	/**
	 * 
	 * @param str   json数据格式
	 * @param reqUrl
	 */
	public static String appPost(String str, String reqUrl) throws Exception{
		//发送数据
		HttpURLConnection conn = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(reqUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(false);//是否自动处理重定向
			conn.setRequestMethod("POST");
//			conn.setRequestMethod("PUT");   // Server returned HTTP response code: 405 for URL: http://10.0.3.60:8088/app-server/trans/v7/getTransTn
			
			conn.setRequestProperty("Content-Type","application/x-gzip");
			conn.setRequestProperty("User-Agent", "AutoyolEs_console");
			conn.connect();
//			OutputStream out = conn.getOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(conn.getOutputStream());
			byte[] reqContent = str.getBytes();
			System.out.println("testPost发送内容："+new String(reqContent,"UTF-8"));//{"name":"张三","age":25,"sex":"男","addr":["地址1","地址2"]}
			out.write(reqContent);
			out.flush();
			out.close();
			
			//接收返回数据
			InputStream in = conn.getInputStream();
			GZIPInputStream gzin = new GZIPInputStream(in);
			BufferedReader reader = new BufferedReader(new InputStreamReader(gzin,"UTF-8"));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in,"gbk"));
			String line;
			while ((line = reader.readLine()) != null) {
//			    System.err.println("client received:"+line);
				sb.append(line);
			}
			reader.close();
			//conn.disconnect();       //{"resCode":"000000","resMsg":"success","data":{"serverTime":"20140804172747557"}}
			return sb.toString();
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		} finally{
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	
	
	
	public static String appPost2(String str, String reqUrl) throws Exception{
		//发送数据
		HttpURLConnection conn = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(reqUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(false);//是否自动处理重定向
			conn.setRequestMethod("POST");
//			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type","application/x-gzip");
			conn.setRequestProperty("User-Agent", "AutoyolEs_console");
			conn.connect();
			GZIPOutputStream out = new GZIPOutputStream(conn.getOutputStream());
			byte[] reqContent = str.getBytes();
//			System.out.println("testPost发送内容："+new String(reqContent,"UTF-8"));//{"name":"张三","age":25,"sex":"男","addr":["地址1","地址2"]}
			out.write(reqContent);
			out.flush();
			out.close();
			
			//接收返回数据
			InputStream in = conn.getInputStream();
			GZIPInputStream gzin = new GZIPInputStream(in);
			BufferedReader reader = new BufferedReader(new InputStreamReader(gzin,"UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
//			    System.err.println("client received:"+line);
				sb.append(line);
			}
			reader.close();
			//conn.disconnect();       //{"resCode":"000000","resMsg":"success","data":{"serverTime":"20140804172747557"}}
			return sb.toString();
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		} finally{
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	/**
	 * 
	 * @param jsonBean 
	 * @param reqUrl 
	 * @param method POST PUT GET DELETE...
	 * @return
	 * @throws Exception
	 */
	private static ObjectMapper mapper = new ObjectMapper();
	public static String appPost(Object jsonBean, String reqUrl,String method) throws Exception{
		//发送数据
				HttpURLConnection conn;
				try {
					URL url = new URL(reqUrl);
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setUseCaches(false);
					conn.setInstanceFollowRedirects(false);//是否自动处理重定向
					conn.setRequestMethod(method);
//					conn.setRequestProperty("User-Agent", "AutoyolEs_console");
					conn.setRequestProperty("User-Agent", "AutoyolEs_web");
					conn.setRequestProperty("Content-Type","application/x-gzip");
					
//					conn.setRequestProperty("Host", "api.zuzuche.com");
//					conn.setRequestProperty("Connection", "keep-alive"); 
//					conn.setRequestProperty("Authorization", "Basic TDMwNjUxMTc1LWhQQCZ9fjpjQklXWjEmOw==");
					conn.connect();
					if(!method.equalsIgnoreCase("DELETE")){
						//有参数才压缩
						if(jsonBean!=null){
							GZIPOutputStream out = new GZIPOutputStream(conn.getOutputStream());
							byte[] reqContent = null;
							if(jsonBean instanceof String){
								reqContent = ((String)jsonBean).getBytes();
							}else{
								reqContent = mapper.writeValueAsBytes(jsonBean);
							}
							logger.info("testPost发送内容："+new String(reqContent,"UTF-8"));
							out.write(reqContent);
							out.flush();
							out.close();
						}
					}
					
					//接收返回数据
					InputStream in = conn.getInputStream();
					GZIPInputStream gzin = new GZIPInputStream(in);
					BufferedReader reader = new BufferedReader(new InputStreamReader(gzin,"UTF-8"));
					
//					BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
					
					String line;
					StringBuffer sb=new StringBuffer();
					while ((line = reader.readLine()) != null) {
					    sb.append(line);
					}
					reader.close();
					conn.disconnect();
					return sb.toString();
				} catch (Exception e) {
					throw e;
				}
	}
}
 