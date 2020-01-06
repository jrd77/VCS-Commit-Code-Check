package com.atzuche.order.admin.util.oss;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** 
 * @comments 图片访问的基础路径： http://at-images.oss-cn-hangzhou.aliyuncs.com/
 *                 测试环境： http://at-images-test.oss-cn-hangzhou.aliyuncs.com/
 * @author  zg 
 * @version 创建时间：2014年2月25日
 */
public class OSSUtils {
	private static Logger logger = LoggerFactory.getLogger(OSSUtils.class);
	
	private static final String ACCESS_ID = "XakvqZLxRN1DR9Iy";
	private static final String ACCESS_KEY = "6GNGlPXmKe3uKC1GQJs5avnRHuOeE2";
	
    /** 普通bucket */
	private static final String BUCKET = "at-images-test";
	/** 需授权访问的 bucket */
	private static final String BUCKET_AUTH = "veri-images-test";
	/** 需授权访问的 bucket car */
	private static final String BUCKET_CAR_AUTH = "veri-images-car-test";
	
    //OSS cilent
    private static final OSSClient client = new OSSClient("http://oss-cn-hangzhou.aliyuncs.com", ACCESS_ID, ACCESS_KEY);
    
	private static MessageDigest MD5 = null;
	
	//----------------------------------------------------------------------------------------------------
	
	public static final String BUCKET_URL = "https://" + BUCKET + "." + "oss-cn-hangzhou.aliyuncs.com" + "/";
	public static final String BUCKET_AUTH_URL = "http://" + BUCKET_AUTH + "." + "oss-cn-hangzhou.aliyuncs.com" + "/";
	public static final String BUCKET_AUTH_CAR_URL = "http://" + BUCKET_CAR_AUTH + "." + "oss-cn-hangzhou.aliyuncs.com" + "/";
	
	
	/** 车辆图片 （ car/YY/MM/"carNo"/"picName" ）  */
	public static final String CAR_PIC_DIR = "car";
	/** 交易过程中的车辆状况图片 */
	public static final String TRANS_PIC_DIR = "trans";
	/** 用户头像  */
	public static final String MEM_PORTRAIT = "portrait";
	
	/** 用户身份认证图片  */
	public static final String MEM_AUTH = "memAuth";
	/** 车辆行驶证  */
	public static final String CAR_LIC = "carLic";

	private static  List<String> HEAD_IMAGE_KEY;

	public static final String BUCKET_URL_NOS = "http://" + BUCKET + "." + "oss-cn-hangzhou.aliyuncs.com" + "/";

    static {
    	try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    }
    public static List<String> getHeadImageKey(){
    	if(HEAD_IMAGE_KEY==null){
    		//初始化所有头像key
    		HEAD_IMAGE_KEY = new ArrayList<String>();
    		ObjectListing objectList = client.listObjects(BUCKET, "headImage");
    		List<OSSObjectSummary> list = objectList.getObjectSummaries();
    	    for (OSSObjectSummary objectSummary : list) {
    	    	HEAD_IMAGE_KEY.add(objectSummary.getKey());
    	    }
    	}
    	return HEAD_IMAGE_KEY;
    }
    
    public static boolean uploadMultipartFile(String key, MultipartFile multifile) throws Exception{
    	boolean errorCode = false;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
        InputStream input = multifile.getInputStream();
        InputStream inputTmp = multifile.getInputStream();
    	try {
	        objectMeta.setContentLength(input.available());
	        //校验文件MD5值
			PutObjectResult res = client.putObject(BUCKET, key, input, objectMeta);
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
			if(inputTmp != null){
				inputTmp.close();
			}
		}
        return errorCode;
    }
    
    /**
     * 
     * @param key
     * @param bfImg
     * @return 文件MD5
     * @throws Exception
     */
    public static String uploadInputStream(String key, InputStream input) throws Exception{
    	String md5 = null;
		try {
			ObjectMetadata objectMeta = new ObjectMetadata();
			objectMeta.setContentType("image/*");//在metadata中标记文件类型
			objectMeta.setContentLength(input.available());
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
			md5 = res.getETag();
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			if(input != null){
				input.close();
			}
		}
        return md5;
    }
    
    /**
     * 
     * @param key
     * @param bfImg
     * @return 文件MD5
     * @throws IOException 
     * @throws Exception
     * 需要手动关闭流
     */
    public static String uploadInputStream(String key, InputStream input,int type) throws IOException{
    	ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentType("image/*");//在metadata中标记文件类型
		objectMeta.setContentLength(input.available());
		String bucket = BUCKET;
		if(type==1){
			bucket = BUCKET;
		}else if(type==2){
			bucket = BUCKET_AUTH;
		}else if(type==3){
			bucket = BUCKET_CAR_AUTH;
		}
		PutObjectResult	res = client.putObject(bucket, key, input, objectMeta);
        return res.getETag();
    }
    
    /**
     * 上传图片（width 或 height 为 null 时不压缩尺寸）
     * @param size 图片尺寸：1、2、3（原图800 x 525） 
     * @param key 图片名称
     * @param image 图片
     * @return
     * @throws Exception
     */
   /* public static boolean uploadBufferedImage(int size, String key, BufferedImage image) throws Exception{
    	if(size != 3){
    		key = size + key;//小图为"1"+key，中图为"2"+key
    	}
    	//修改尺寸
    	BufferedImage bfImg = PicUtils.modifySize(size, image);
    	if(size == 2 || size == 3){
    		//添加水印
    		bfImg = PicUtils.watermarkImage(size,bfImg);
    	}
    	//转成inputStream
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos); 
		ImageIO.write(bfImg, "jpg", ios); //图片写入到 ImageOutputStream

    	boolean errorCode = false;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/jpg");//在metadata中标记文件类型
        
        InputStream input = new ByteArrayInputStream(bos.toByteArray());
        InputStream inputTmp = new ByteArrayInputStream(bos.toByteArray());
    	try {
	        objectMeta.setContentLength(input.available());
	        //校验文件MD5值
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
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
    }*/
    
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
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
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
    /**
     * 过时方法仅限于JPG图片、谨慎使用、
     * 需上传其他格式使用方法uploadInputStream
     * @param key
     * @param image
     * @return
     * @throws Exception
     */
    public static boolean uploadBufferedImage1NoWater(String key, BufferedImage image) throws Exception{
//    	if(size != 3){
//    		key = size + key;//小图为"1"+key，中图为"2"+key
//    	}
    	//修改尺寸并添加水印添加水印
//    	image = PicUtils.resizeAndPressWatermark(size, image);
    	
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
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
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
    
    public static boolean uploadInputStreamAuth(String key, MultipartFile multifile) throws Exception{
    	boolean errorCode = false;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
        InputStream input = multifile.getInputStream();
        InputStream inputTmp = multifile.getInputStream();
    	try {
	        objectMeta.setContentLength(input.available());
	        //校验文件MD5值
			PutObjectResult	res = client.putObject(BUCKET_AUTH, key, input, objectMeta);
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
			if(inputTmp != null){
				inputTmp.close();
			}
		}
        return errorCode;
    }
    
    
    public static boolean uploadInputStreamAuth(String key, BufferedImage image) throws Exception{
    	boolean errorCode = false;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
        
//        InputStream input = multifile.getInputStream();
//        InputStream inputTmp = multifile.getInputStream();
        
        //--------------------
        //转成inputStream
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos); 
		ImageIO.write(image, "jpg", ios); //图片写入到 ImageOutputStream
		
        InputStream input = new ByteArrayInputStream(bos.toByteArray());
        InputStream inputTmp = new ByteArrayInputStream(bos.toByteArray());
        //--------------------
        
    	try {
	        objectMeta.setContentLength(input.available());
	        //校验文件MD5值
			PutObjectResult	res = client.putObject(BUCKET_AUTH, key, input, objectMeta);
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
			if(inputTmp != null){
				inputTmp.close();
			}
		}
        return errorCode;
    }
    
    /**
     * 上传文件 至 bucket ay-images
     * @param key
     * @param base64
     * @param verifyCode 文件MD5值
     * @return
     * @throws Exception
     */
    public static boolean uploadBase64(String key, String base64, String verifyCode) throws Exception{
    	boolean errorCode = false;
    	InputStream	input = null;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
    	try {
	        byte[] bytes = new BASE64Decoder().decodeBuffer(base64);
	        objectMeta.setContentLength(bytes.length);
	        input = new ByteArrayInputStream(bytes);
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
			//校验文件MD5值
			if(verifyCode.equalsIgnoreCase(res.getETag()) == true){
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
		}
        return errorCode;
    }
    
    /**
     * 上传文件 至 bucket ay-images
     * @param key
     * @param base64
     * @param verifyCode 文件MD5值
     * @return
     * @throws Exception
     */
    public static boolean uploadAuthBase64(String key, String base64, String verifyCode) throws Exception{
    	boolean errorCode = false;
    	InputStream	input = null;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
    	try {
	        byte[] bytes = new BASE64Decoder().decodeBuffer(base64);
	        objectMeta.setContentLength(bytes.length);
	        input = new ByteArrayInputStream(bytes);
			PutObjectResult	res = client.putObject(BUCKET_AUTH, key, input, objectMeta);
			//校验文件MD5值
			if(verifyCode.equalsIgnoreCase(res.getETag()) == true){
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
		}
        return errorCode;
    }
    
    /**
     * 上传文件 至 bucket ay-images
     * @param key
     * @param base64
     * @param verifyCode 文件MD5值
     * @return
     * @throws Exception
     */
    public static boolean uploadAuthBase64(String key, String base64) throws Exception{
    	boolean errorCode = false;
    	InputStream	input = null;
    	ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentType("image/*");//在metadata中标记文件类型
    	try {
	        byte[] bytes = new BASE64Decoder().decodeBuffer(base64);
	        objectMeta.setContentLength(bytes.length);
	        input = new ByteArrayInputStream(bytes);
			PutObjectResult	res = client.putObject(BUCKET, key, input, objectMeta);
			//校验文件MD5值
//			if(verifyCode.equalsIgnoreCase(res.getETag()) == true){
//				logger.info("文件上传成功：{}",key);
//				errorCode = true;
//			}else{
//				logger.error("文件上传失败：{}",key);
//			}
			errorCode = false;
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			if(input != null){
				input.close();
			}
		}
        return errorCode;
    }
    
    public static void upload(String key, String fileName) throws FileNotFoundException{
    	File file = new File(fileName);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        // 可以在metadata中标记文件类型
        objectMeta.setContentType("image/jpeg");

        InputStream input = new FileInputStream(file);
        client.putObject(BUCKET, key, input, objectMeta);
    }
    
    public static void uploadCar(String key, String fileName) throws FileNotFoundException{
    	File file = new File(fileName);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        // 可以在metadata中标记文件类型
        objectMeta.setContentType("image/jpeg");

        InputStream input = new FileInputStream(file);
        client.putObject(BUCKET_CAR_AUTH, key, input, objectMeta);
    }
    
    public static void uploadUsedCarContract(String key, MultipartFile multifile) throws IOException{
    	ObjectMetadata objectMeta = new ObjectMetadata();
    	// 可以在metadata中标记文件类型
        objectMeta.setContentType("application/octet-stream");
        InputStream input =multifile.getInputStream();
        objectMeta.setContentLength(input.available());
        client.putObject(BUCKET, key, input, objectMeta);
    }
    
    
    /*public static void downLoad(String key){
    	client.getObject(new GetObjectRequest(BUCKET, "test/test.jpg"),new File("C:\\Users\\lsl\\Desktop\\兰博\\lanbonew.jpg"));
    }*/
    
    public static void getPicObj(String key) throws Exception{
    	OSSObject object = client.getObject(BUCKET, key);
    	InputStream objectContent = object.getObjectContent();
    	// 关闭流
        objectContent.close();
    }
    
    public static InputStream getNormalPicObj(String key) throws Exception{
    	OSSObject object = client.getObject(BUCKET, key);
    	InputStream objectContent = object.getObjectContent();
    	return objectContent;
    }
    
    public static OSSObject getOSSObject(String key){
    	return client.getObject(BUCKET,key);
    }

	/**
	 * 使用之后 关闭 inputStream
	 *
	 * @param key
	 * @throws IOException
	 */
	public static InputStream getAuthPicObj(String key) throws IOException {
		OSSObject object = client.getObject(BUCKET_AUTH, key);
		ObjectMetadata objectMetadata = object.getObjectMetadata();
		// 获取ObjectMeta
		InputStream objectContent = object.getObjectContent();
		// 关闭流
		//objectContent.close();
		return objectContent;
	}

	/**
	 * 使用之后 关闭 inputStream
	 *
	 * @param key
	 * @throws IOException
	 */
	public static InputStream getCarAuthPicObj(String key) throws IOException {
		OSSObject object = client.getObject(BUCKET_CAR_AUTH, key);
		ObjectMetadata objectMetadata = object.getObjectMetadata();
		// 获取ObjectMeta
		InputStream objectContent = object.getObjectContent();
		// 关闭流
		//objectContent.close();
		return objectContent;
	}

	public static OSSObject getOSSObjectAuth(String key){
    	OSSObject object = client.getObject(BUCKET_AUTH, key);
    	return object;
    }
    
    public static ObjectMetadata getFile(String bucket, String key, String fileNameWithPath){
    	GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
    	// 下载Object到文件
    	ObjectMetadata objectMetadata = client.getObject(getObjectRequest, new File(fileNameWithPath));
    	
    	
    	return objectMetadata;
    }

    public static InputStream getCarSourcePic(String key){
    	OSSObject object = client.getObject(BUCKET_CAR_AUTH, key);
    	InputStream objectContent = object.getObjectContent();
    	return objectContent;
    }
    /**  
     * 下载远程文件并保存到本地  
     * @param remoteFilePath 远程文件路径   
     * @param localFilePath 本地文件路径  
     */
    /*public static void downloadFile(String remoteFilePath, Integer carNo, String basePath, String picKey)
    {
    	logger.info("downloadFile remoteFilePath:" + remoteFilePath);
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        String filePath = createDir(carNo,basePath);
        logger.info("downloadFile filePath:" + filePath);
        String localFilePath = filePath + picKey;
        logger.info("downloadFile localFilePath:" + localFilePath);
        File f = new File(localFilePath);
        try
        {	
        	if(f!=null && !f.exists()){
                f.createNewFile();
            }
        	
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpURLConnection)urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1)
            {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        	logger.error("downloadFile Exception：",e);
        }
        finally
        {
            try
            {
                bis.close();
                bos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }*/

    public static boolean getCarPic(Integer carNo,String basePath, List listKey, List<String> otherKey,List<String> carOwnerPhotoKey){
    	try {
    		//logger.info("getCarPic size:" + listKey.size());
    		logger.info("getCarPic basePath:" + basePath);
    		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_CAR_AUTH);

    		String dir = carNo + "/";
    		// 设置参数
    		// "/" 为文件夹的分隔符
    		listObjectsRequest.setDelimiter("/");
    		listObjectsRequest.setPrefix(dir);
    		
    		ObjectListing listing = client.listObjects(listObjectsRequest);
    		
    		String filePath = createDir(carNo,basePath);
    		logger.info("getCarPic filePath:" + filePath);

    	    // 遍历所有Object
			if(CollectionUtils.isNotEmpty(listKey)) {
				for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
					if(listKey.contains(objectSummary.getKey())){
						try{
							//循环下载
							ObjectMetadata mm = getFile(BUCKET_CAR_AUTH, objectSummary.getKey(), filePath+objectSummary.getKey());
							//远程下载文件到本地。 这种方法行不通~~!
							//    	    		downloadFile(SysConf.CON_SERVER_ADDR+filePathNew+objectSummary.getKey(),carNo,"C:/AtCarPic/",objectSummary.getKey());
						} catch (Exception e) {
							logger.error("car文件下载报错： " , e);
							continue;  //跳出该次循环
						}
					}
				}
			}

    	    
    	    //附加下载 行驶证，交强险，商业险，保养图片。 huangjing 140826
    	    if(otherKey != null && otherKey.size() > 0){
    	    	for (String key : otherKey) {
    	    		if(key != null && key.length() > 0){  //判断文件名是否非空~~!
	    	    		int idx = key.lastIndexOf("/");
	//    	    		System.out.println("i:" + i);
	    	    		String fileName = key.substring(idx);
	//    	    		System.out.println("s:" + s);
	    	    		
	    	    		try {
	    	    			//循环下载
		    	    		ObjectMetadata mm = getFile(BUCKET_AUTH, key, filePath + carNo + fileName );
						} catch (Exception e) {
							logger.error("other文件下载报错： " , e);
							continue;  //跳出该次循环
						}
    	    		}
				}
    	    }

			//附加下载 个性照片。 zhaodongdong 180713
			if (CollectionUtils.isNotEmpty(carOwnerPhotoKey)) {
				int picNum = 1;
				for (String key : carOwnerPhotoKey) {
					if (key != null && key.length() > 0) {
						int idx = key.lastIndexOf(".");
						String fileName = "/individuation" + picNum + key.substring(idx);
						picNum++;
						try {
							//循环下载
							ObjectMetadata mm = getFile(BUCKET, key, filePath + carNo + fileName);
						} catch (Exception e) {
							logger.error("other文件下载报错： ", e);
							continue;  //跳出该次循环
						}
					}
				}
			}
    	    
		} catch (Exception e) {
			logger.error("图片下载时报错：",e);
			return false;
		}
    	return true;
    }


	public static boolean getCarGpsPic(Integer carNo, String basePath, List<String> listKey) {
		try {

			String filePath = createDir(carNo, basePath);
			logger.info("getCarPic filePath:" + filePath);

			//附加下载 gps设备照片。 zhaodongdong 180726
			if (CollectionUtils.isNotEmpty(listKey)) {
				int picNum = 1;
				for (String key : listKey) {
					if (key != null && key.length() > 0) {
						int idx = key.lastIndexOf(".");
						String fileName = "/deviceImage" + picNum + key.substring(idx);
						picNum++;
						try {
							//循环下载
							ObjectMetadata mm = getFile(BUCKET, key, filePath + carNo + fileName);
						} catch (Exception e) {
							logger.error("other文件下载报错： ", e);
							continue;  //跳出该次循环
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("图片下载时报错：", e);
			return false;
		}
		return true;
	}

	public static String getDir(Integer carNo,String basePath){
    	/**按年月日来分*/
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);//得到年
		int currentMonth = cal.get(Calendar.MONTH)+1;//得到月
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);//得到日
		return  basePath+currentYear+"-"+currentMonth+"-"+currentDay+"/";
    }
    
    private static String createDir(Integer carNo,String basePath) {
    	String dir = carNo + "/";
    	/**按年月日来分*/
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);//得到年
		int currentMonth = cal.get(Calendar.MONTH)+1;//得到月
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);//得到日
		
		
//		String basePath = "C:/AtCarPic/";
		String filePath = basePath+currentYear+"-"+currentMonth+"-"+currentDay+"/";
		File file = new File(basePath);
		if(!file.exists()){
			file.mkdir();
		}
		File file2 = new File(filePath);
		if(!file2.exists()){
			file2.mkdir();
		}
		File file3 = new File(filePath+dir);
		if(!file3.exists()){
			file3.mkdir();
		}
		return filePath;
	}

	public static void deleteOSSObject(String key){
    	client.deleteObject(BUCKET, key);
    }

    public static void deleteOSSObjectAuth(String key){
    	client.deleteObject(BUCKET_AUTH, key);
    }

    public static void deleteOSSObjectCarAuth(String key){
    	client.deleteObject(BUCKET_CAR_AUTH, key);
    }

    public static void copyOSSObject(String sourceKey,String destinationKey){
        client.copyObject(BUCKET, sourceKey, BUCKET, destinationKey);
    }
    public static void copyOSSObjectAuth(String sourceKey,String destinationKey){
        client.copyObject(BUCKET, sourceKey, BUCKET_AUTH, destinationKey);
    }
    public static void copyOSSObjectCarAuth(String sourceKey,String destinationKey){
        client.copyObject(BUCKET, sourceKey, BUCKET_CAR_AUTH, destinationKey);
    }
	public static void copyOSSObjectCarAuth1(String sourceKey,String destinationKey){
		client.copyObject(BUCKET_CAR_AUTH, sourceKey, BUCKET_CAR_AUTH, destinationKey);
	}
	public static void copyOSSObjectCarAuth2(String sourceKey,String destinationKey){
		client.copyObject(BUCKET_CAR_AUTH, sourceKey, BUCKET, destinationKey);
	}
    //============================ private methods =========================

    /**
     * 获取文件的MD5值
     * @return md5串
     */
    private static String getMD5(File file) {
        FileInputStream in = null;
        try {
        	in = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
            	MD5.update(buffer, 0, len);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
            return null;
        } catch (IOException e) {
        	e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null){
                	in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileInputStreamMD5(InputStream in) throws Exception {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
        	MD5.update(buffer, 0, len);
        }
        return new String(Hex.encodeHex(MD5.digest()));
    }


	public static void main2(String[] args) throws Exception {
//		deleteOSSObjectCarAuth("92265667/3.png");
//		deleteOSSObjectAuth("14/08/92265667/vehicleLic.png");
//		System.err.println("==============删除图片测试阿里云不存在图片的情况下，与数据库记录不匹配。");

		//String fileName = "C:\\Users\\lsl\\Desktop\\heiseche.jpg";
		/*String fileName1 = "E:/workspace/autoyol_dir/车辆照片/压缩/图片6.jpg";
		String fileName2 = "E:/workspace/autoyol_dir/车辆照片/压缩/图片7.jpg";
		String fileName3 = "E:/workspace/autoyol_dir/车辆照片/压缩/图片10.jpg";*/

		//上传车辆，无损压缩。  env=1 切换到正式环境。
//		upload("car/14/09/673858626/1410936899873.jpg", "C:/Users/huangjing/Desktop/封面/xh.jpg");  //car/14/09/673858626/1410946529125.jpg

//		upload("car/14/09/673858626/1410946529125.jpg", "C:/Users/huangjing/Desktop/封面/xh.jpg");
//		upload("car/14/09/363962681/1410946858559.jpg", "C:/Users/huangjing/Desktop/封面/ll.jpg");
//		upload("car/14/09/593006042/1410946985048.jpg", "C:/Users/huangjing/Desktop/封面/dy.jpg");
//		upload("car/14/09/852828198/1410947080202.jpg", "C:/Users/huangjing/Desktop/封面/ym.jpg");
//		upload("car/14/09/280572042/1410947371858.jpg", "C:/Users/huangjing/Desktop/封面/fm.jpg");
		upload("car/14/09/746347670/1410947458943.jpg", "C:/Users/huangjing/Desktop/封面/yj.jpg");

		System.err.println("上传图片完成");
		//下载
//		ObjectMetadata mm = getFile(BUCKET, "car/14/09/673858626/1410936880800.jpg", "E:/75.jpg");
//		System.err.println("下载： "+ mm.getETag());

		//String key = "test/test.jpg";
		//String key = "test/test1.jpg";
		/*upload("car/14/01/531248690/图片6.jpg", fileName1);
		upload("car/14/01/531248690/图片7.jpg", fileName2);
		upload("car/14/01/531248690/图片10.jpg", fileName3);*/

		//upload("car/14/01/218867609/20101121joanne001.jpg", "E:/workspace/autoyol_dir/车辆照片/福特蒙迪欧致胜/yasuo/20101121joanne001.jpg");

		//上传图片
//		upload("car/14/05/22/lbjn.jpg", "E:/lbjn.jpg");
//		uploadCar("154988354/0.jpg", "E:/lbjn.jpg");
//		uploadCar("154988354/1.jpg", "E:/_0001_GMC1.jpg");
//		uploadCar("76264697/0.jpg", "E:/image001.jpg");
//		uploadCar("76264697/1.jpg", "E:/klz.png");
//		System.err.println("上传成功~~~!!!");

		//downLoad();

		/*deleteOSSObject("14/01/531248690/图片6.jpg");
		deleteOSSObject("14/01/531248690/图片8.jpg");
		deleteOSSObject("14/01/531248690/图片7.jpg");*/

		//System.out.println(new File(fileName).length());
		/*String base64 = CommonUtils.imgToBase64(fileName);
		System.out.println(upload(key, base64));*/

		//System.out.println(bytes.length);

		/*System.out.println(getMD5(new File(fileName)));
		System.out.println(getFileInputStreamMD5(new FileInputStream(new File(fileName))));*/

		/*System.out.println(new File(fileName).length());
		System.out.println(new FileInputStream(new File(fileName)).available());*/
//		getFile(BUCKET_AUTH, "mem/14/03/820345060/portrait.png", "E:\\portrait.png");

		//下载图片
//		ObjectMetadata mm = getFile(BUCKET, "car/14/05/22/lbjn.jpg", "E:/download-lbjn.png");
//		ObjectMetadata mm = getFile(BUCKET, "car/14/05/22/lbjn.jpg", "E:/555.jpg");
//		System.out.println("下载： "+ mm.getETag());

//		ObjectMetadata mm = getFile(BUCKET_AUTH, "14/08/388189342/vehicleLic.png", "D:/555.jpg");
//		System.err.println("@@: " + mm.getContentLength());

//		ObjectMetadata mm = getFile(BUCKET_AUTH, "14/08/50556952/insurance.jpg", "D:/666.jpg");
//		System.err.println("@@: " + mm.getContentLength());


//		List<Bucket> buckets = client.listBuckets();
//		 //遍历Bucket
//		for (Bucket bucket : buckets) {
//		    System.out.println(bucket.getName());
//		}

		//client.deleteBucket("auth-images");
		//client.deleteBucket("ay-images");
		//删除
//		client.deleteBucket("at-images-test2");

		//验证
//		boolean check = client.doesBucketExist("veri-images-car");
//		if(check){
//			System.err.println("存在哦~!!");
//		}else{
//			System.err.println("不存在~!!");
//		}

		// 获取指定bucket下的所有Object信息
//	    ObjectListing listing = client.listObjects("veri-images-test");

//		ObjectMetadata mm = getFile(BUCKET_CAR_AUTH, "154988354/10.jpg", "E:/10.jpg");

		// 构造ListObjectsRequest请求
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest("veri-images-test");
		System.err.println("BUCKET_CAR_AUTH: " + BUCKET_CAR_AUTH);
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_CAR_AUTH);

//		String dir = "656922443/";
//		String dir = "50556952/";
//		String dir = "156706778/";
		String dir = "92265667/";

		// 设置参数
		// "/" 为文件夹的分隔符
//		listObjectsRequest.setDelimiter("/");
//		listObjectsRequest.setMarker("123");
//		listObjectsRequest.setDelimiter("/");
//		listObjectsRequest.setMaxKeys(999);  //最大999张
		// 递归列出fun目录下的所有文件
//		listObjectsRequest.setPrefix("mem/14/07/");

		listObjectsRequest.setDelimiter("/");
		listObjectsRequest.setPrefix(dir);


		ObjectListing listing = client.listObjects(listObjectsRequest);


//		ObjectListing listing = client.listObjects("veri-images-test");
		/**按年月日来分*/
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);//得到年
		int currentMonth = cal.get(Calendar.MONTH)+1;//得到月
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);//得到月

		String basePath = "C:/AtCarPic/";
		String filePath = basePath+currentYear+"-"+currentMonth+"-"+currentDay+"/";
		File file = new File(basePath);
		if(!file.exists()){
			file.mkdir();
		}
		File file2 = new File(filePath);
		if(!file2.exists()){
			file2.mkdir();
		}
		File file3 = new File(filePath+dir);
		if(!file3.exists()){
			file3.mkdir();
		}
		
	    // 遍历所有Object
		System.out.println("Objects:");
	    for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
	    	//循环显示
	        System.out.println(objectSummary.getKey());
	        //deleteOSSObject(objectSummary.getKey());
	        //循环下载
	        //ObjectMetadata mm = getFile(BUCKET_CAR_AUTH, objectSummary.getKey(), filePath+objectSummary.getKey());
	    }
		
	    // 遍历所有CommonPrefix
	    /*System.out.println("CommonPrefixs:");
	    for (String commonPrefix : listing.getCommonPrefixes()) {
	        System.out.println(commonPrefix);
	    }*/

	    
		//创建
		//client.createBucket("at-images-test2");
		//client.createBucket("veri-images-test");
		
		//设置权限
		//CannedAccessControlList是枚举类型，包含三个值： Private 、 PublicRead 、 PublicReadWrite
		//client.setBucketAcl("at-images-test", CannedAccessControlList.PublicRead);


	}
	
	public static void main(String[] args){
//		ObjectMetadata mm = getFile(BUCKET, "89984822/3.jpg", "E:/752.jpg");
//		System.err.println("下载： "+ mm.getETag());
		
		//车辆图片
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_CAR_AUTH);
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_AUTH);
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET);
//		listObjectsRequest.setDelimiter("/");
//		listObjectsRequest.setPrefix("89984822/");  //
		
		listObjectsRequest.setPrefix("17/11/89984822/"); //已删除。
		
		ObjectListing listing = client.listObjects(listObjectsRequest);
		 for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
		    	//循环显示
		        System.out.println("=="+objectSummary.getKey());
		        
		        //deleteOSSObject(objectSummary.getKey()); //原始的图片，暂时保留。
		        //车辆图片，已删除。
//		        deleteOSSObjectCarAuth(objectSummary.getKey());
		        
		        //循环下载
		        //ObjectMetadata mm = getFile(BUCKET_CAR_AUTH, objectSummary.getKey(), filePath+objectSummary.getKey());
		    }
		 
		
		 //删除
//		deleteOSSObjectCarAuth("92265667/3.png");
//		
//		//车辆图片
//		ObjectMetadata mm = getFile(BUCKET_CAR_AUTH, "89984822/3.jpg", "E:/100.jpg");
//		System.err.println("下载： "+ mm.getETag());
		
	}
	
	
	public static void main3(String[] args) {
		
		System.out.println(System.getProperty("java.io.tmpdir"));
		List<String> listKey=new ArrayList<String>();
			listKey.add("carDetect/153136186/1488442115736/1488442175236.jpg");
			listKey.add("carDetect/153136186/1488442115736/1488442208355.jpg");
	    	try {
	    		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET);
	    		String dir ="carDetect/153136186/1488442115736/";
	    		// 设置参数
	    		// "/" 为文件夹的分隔符
	    		listObjectsRequest.setDelimiter("/");
	    		listObjectsRequest.setPrefix(dir);
	    		
	    		ObjectListing listing = client.listObjects(listObjectsRequest);
	    		// 遍历所有Object
	    	    for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
	    	    	if(listKey.contains(objectSummary.getKey())){
	    	    		try{
	    	    			String name=objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/"));
		    	    		//循环下载
		    	    		ObjectMetadata mm = getFile(BUCKET, objectSummary.getKey(),System.getProperty("java.io.tmpdir")+"/carDetect/1"+name);
		    	    	} catch (Exception e) {
							logger.error("car文件下载报错： " , e);
							continue;  //跳出该次循环
						}
	    	    	}
	    	    }
	    	   
	    	    }catch (Exception e) {
					logger.error("car文件下载报错： " , e);
				}
	    	}

	/**
	 * 
	 * @param filePath 文件下载路径
	 * @param listKey  图片路径+图片文件名
	 * @param dir	图片路径
	 * @return
	 */
	public static boolean getCarDetectPic(String filePath, List listKey,String dir){
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET);
    		// 设置参数
    		// "/" 为文件夹的分隔符
    		listObjectsRequest.setDelimiter("/");
    		listObjectsRequest.setPrefix(dir);
    		ObjectListing listing = client.listObjects(listObjectsRequest);
    		
    		logger.info("getCarPic filePath:" + filePath);
    		for(OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
	    	    if(listKey.contains(objectSummary.getKey())){
	    	    	try{
	    	    		String name=objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/")+1);
		    	    	//循环下载
		    	    	ObjectMetadata mm = getFile(BUCKET, objectSummary.getKey(),filePath+name);
		    	    } catch (Exception e) {
						logger.error("carDetect文件下载报错： " , e);
						continue;  //跳出该次循环
					}
	    	    }
    		}
		} catch (Exception e) {
			logger.error("carDetect文件下载报错： " , e);
		}
		return true; 
	}

}
 