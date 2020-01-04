package com.atzuche.order.delivery.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/** 
 * @comments 
 * @author  zg 
 * @version 创建时间：2014年2月25日
 */
public class OSSUtils implements AutoCloseable{
	private static Logger logger = LoggerFactory.getLogger(OSSUtils.class);
	
	private static final String ACCESS_ID = "XakvqZLxRN1DR9Iy";
    private static final String ACCESS_KEY = "6GNGlPXmKe3uKC1GQJs5avnRHuOeE2";
    
	/** 需授权访问的 bucket */
	public static final String BUCKET_AUTH = "veri-images-test";
	/** 需授权访问的car bucket */
	public static final String USER_CAR_BUCKET = "veri-images-test";
    private static final OSSClient client = new OSSClient("http://oss-cn-hangzhou.aliyuncs.com", ACCESS_ID, ACCESS_KEY);
    
    private static MessageDigest MD5 = null;

	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

    @Override
	public void close() throws Exception {
		
	}
    
    /**
     * 上传文件 至 bucket at-images
     * @param key
     * @param base64
     * @param verifyCode 文件MD5值
     * @return
     * @throws Exception
     */
    public static boolean uploadAuth(String key, String base64, String verifyCode) throws Exception{
    	InputStream	input = null;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");
    	try {
	        byte[] bytes = Base64.decodeBase64(base64);
	        objectMeta.setContentLength(bytes.length);
	        input = new ByteArrayInputStream(bytes);
			PutObjectResult res = client.putObject(BUCKET_AUTH, key, input, objectMeta);
			logger.info("Res MD5:"+res.getETag());
			return true;
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}finally{
			if(input != null){
				input.close();
			}
		}
    }

    /**
     * 创建有访问权限的bucket
     * @param bucketName
     */
    @SuppressWarnings("unused")
	private static void createAuthBucket(String bucketName){
    	client.createBucket(bucketName);
    	client.setBucketAcl(bucketName, CannedAccessControlList.Private);
    }
    
    /**
     * 创建 public read 的bucket
     * @param bucketName
     */
    @SuppressWarnings("unused")
	private static void createBucket(String bucketName){
    	client.createBucket(bucketName);
    	client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    }
    
    /**
     * 列出所有bucket
     */
    @SuppressWarnings("unused")
	private static void listAllBuckets(){
    	List<Bucket> buckets = client.listBuckets();
    	// 遍历Bucket
    	for (Bucket bucket : buckets) {
    	    System.out.println(bucket.getName());
    	}

    }

    public static OSSClient getClient(){
		return client;
	}
}
 