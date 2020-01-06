package com.atzuche.order.renterwz.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.atzuche.order.commons.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * OssService
 *
 * @author shisong
 * @date 2019/12/31
 */
@Service
public class OssService implements AutoCloseable {
    private static Logger logger = LoggerFactory.getLogger(OssService.class);

    private static final String ACCESS_ID = "XakvqZLxRN1DR9Iy";
    private static final String ACCESS_KEY = "6GNGlPXmKe3uKC1GQJs5avnRHuOeE2";
    public static final String OSS_END_POINT = "http://oss-cn-hangzhou.aliyuncs.com";

    /** 普通 bucket */
    @Value("${oss.bucket}")
    public String bucket;
    /**
     * OSS cilent
     */
    private static final OSSClient client = new OSSClient(OSS_END_POINT, ACCESS_ID, ACCESS_KEY);

    @Override
    public void close() throws Exception {

    }

    /**
     * 上传文件 至 bucket at-images
     * @param key
     * @param base64
     * @throws Exception
     */
    public Integer upload(String key, String base64) throws Exception{
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "上传文件 至 bucket at-images");
        Integer result = -1;
        InputStream input = null;
        ObjectMetadata objectMeta = new ObjectMetadata();
        //在metadata中标记文件类型
        objectMeta.setContentType("image/*");
        try {
            byte[] bytes = Base64.decodeBase64(base64);
            objectMeta.setContentLength(bytes.length);
            input = new ByteArrayInputStream(bytes);
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OssService.upload");
            Cat.logEvent(CatConstants.FEIGN_PARAM,key);
            PutObjectResult res = client.putObject(bucket, key, input, objectMeta);
            logger.info("Res MD5:"+res.getETag());
            result = 200;
        } catch (Exception e) {
            logger.error("上传文件 至 bucket at-images 异常",e);
            Cat.logError("上传文件 至 bucket at-images 异常",e);
        }finally{
            if(input != null){
                input.close();
            }
            t.complete();
        }
        return result;
    }

    public void deleteOSSObject(String key){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "删除文件 ");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OssService.deleteOSSObject");
            Cat.logEvent(CatConstants.FEIGN_PARAM,key);
            client.deleteObject(bucket, key);
        } catch (OSSException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            logger.error("删除文件 异常",e);
            Cat.logError("删除文件 异常",e);
        } finally {
            t.complete();
        }
    }
}
