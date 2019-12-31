package com.atzuche.order.delivery.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 数据解压缩工具
 * @author zhiping.li
 * @date 2016/06/23
 * @version 1.0
 */
public class ZipUtils {


	// 压缩   
	public static String compress(String str) {   
		if (str == null || str.length() == 0) {   
			return str;   
		}   
		ByteArrayOutputStream out=null;
		GZIPOutputStream gzip =null;
		String result = null;
		try {
			out = new ByteArrayOutputStream();   
			gzip = new GZIPOutputStream(out);   
			gzip.write(str.getBytes(StandardCharsets.UTF_8));
			result = out.toString("UTF-8"); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(gzip);
			IOUtils.closeQuietly(out);
		}   
		return result;   
	}   
	/**
	 * 使用zip进行解压缩
	 * @param
	 * @return 解压后的字符串
	 */
	// 解压缩   
	public static String uncompress(byte[] msg) {

		 
		ByteArrayOutputStream out =  null;
		ByteArrayInputStream in = null;
		GZIPInputStream gunzip = null;
		String result = null;
		try {
			out = new ByteArrayOutputStream();   
			in = new ByteArrayInputStream(msg);   
			gunzip = new GZIPInputStream(in);   
			byte[] buffer = new byte[256];   
			int n;   
			while ((n = gunzip.read(buffer))>= 0) {   
				out.write(buffer, 0, n);   
			}
			result = out.toString("UTF-8");   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(gunzip);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)   
		return result;   
	}   
	public static void main(String[] args) {
		String s = compress("OK");
		System.out.println("压缩之后："+s);
		//System.out.println("解压缩之后："+uncompress(s));
	}
}
