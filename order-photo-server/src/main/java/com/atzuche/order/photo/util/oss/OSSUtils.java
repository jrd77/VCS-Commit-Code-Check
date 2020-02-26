package com.atzuche.order.photo.util.oss;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.atzuche.order.photo.util.SysConfig;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class OSSUtils {
	private static Logger logger = LoggerFactory.getLogger(OSSUtils.class);

    //OSS cilent
    private static final OSSClient client = new OSSClient(SysConfig.ossEndpoint, SysConfig.ossAccessId, SysConfig.ossAccessKey);
    
	private static MessageDigest MD5 = null;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static final String BUCKET_URL = "https://" + SysConfig.ossBucket + "." + "oss-cn-hangzhou.aliyuncs.com" + "/";

    public static boolean uploadBufferedImage1(int size, String key, BufferedImage image) throws Exception{
    	if(size != 3){
    		key = size + key;//小图为"1"+key，中图为"2"+key
    	}
    	//修改尺寸并添加水印添加水印
    	image = PicUtils.resizeAndPressWatermark(size, image);
    	
    	boolean errorCode = false;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/jpg");//在metadata中标记文件类型
        //转成inputStream
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos); 
		ImageIO.write(image, "jpg", ios); //图片写入到 ImageOutputStream
		
        InputStream input = new ByteArrayInputStream(bos.toByteArray());
        InputStream inputTmp = new ByteArrayInputStream(bos.toByteArray());
    	try {
	        objectMeta.setContentLength(input.available());
	        //校验文件MD5值
			PutObjectResult	res = client.putObject(SysConfig.ossBucket, key, input, objectMeta);
			String checkMD5 = res.getETag();
			String verifyCode = getFileInputStreamMD5(inputTmp);
			if(verifyCode.equalsIgnoreCase(checkMD5) == true){
				logger.info("文件上传成功：{}",key);
				errorCode = true;
			}else{
				logger.error("文件上传失败：{}",key);
			}
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			if(input != null){
				input.close();
			}
			if(inputTmp != null){
				inputTmp.close();
			}
		}
        return errorCode;

    }


    private static String getFileInputStreamMD5(InputStream in) throws Exception {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
        	MD5.update(buffer, 0, len);
        }
        return new String(Hex.encodeHex(MD5.digest()));
    }

}
 