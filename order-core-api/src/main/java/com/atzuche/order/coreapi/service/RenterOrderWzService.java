package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.service.OssService;
import com.atzuche.order.renterwz.service.RenterOrderWzIllegalPhotoService;
import com.atzuche.order.renterwz.service.TransIllegalSendAliYunMq;
import com.atzuche.order.renterwz.vo.PhotoUploadVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * RenterOrderWzService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzService {

    private static final Logger logger = LoggerFactory.getLogger(RenterOrderWzService.class);

    @Resource
    private RenterOrderWzIllegalPhotoService renterOrderWzIllegalPhotoService;

    @Resource
    private TransIllegalSendAliYunMq transIllegalSendAliYunMq;

    @Resource
    private OssService ossService;

    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer FAILED_STATUS = 500;


    /**图片上传张数上限**/
    public static final int IMAGE_UPLOAD_LIMIT = 35;

    /**图片上传超过35张给用户的提示**/
    public static final String SURPASS_IMAGE_ERROR_TEXT = "图片只能上传35张";

    /**
     *  500 系统内部异常 200 成功 -1  阿里云上传失败  -2 上传数量大于35张 -3订单不存在 -4您只能上传自己的违章照片
     * @param photoUpload
     * @return
     * @throws Exception
     */
    public Integer upload(PhotoUploadVO photoUpload) throws Exception{
        String orderNo = photoUpload.getOrderNo();
        String basePath = CommonUtils.createTransBasePath(orderNo);
        String str = UUID.randomUUID().toString();
        String key = basePath+"illegal/"+str+".jpg";
        String serialNumber = photoUpload.getSerialNumber();
        String userType = photoUpload.getUserType();
        //TODO
        String carNum = "";
        logger.info("上传文件。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);

        Integer result  = validateOrderInfo(photoUpload.getMemNo(),orderNo,Integer.parseInt(userType));
        if (!SUCCESS_STATUS.equals(result)) {
            return result;
        }
        //生成原图片
        result = ossService.upload(key, photoUpload.getPhotoContent());
        if (SUCCESS_STATUS.equals(result)) {
            logger.info("上传文件到阿里云成功。。。。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
            RenterOrderWzIllegalPhotoEntity photo = new RenterOrderWzIllegalPhotoEntity();
            Date date = new Date();
            photo.setOrderNo(orderNo);
            photo.setPath(key);
            photo.setUserType(Integer.valueOf(photoUpload.getUserType()));
            photo.setCarPlateNum(carNum);
            photo.setCreateTime(date);
            photo.setUpdateTime(date);
            photo.setUpdateOp(String.valueOf(photoUpload.getMemNo()));
            photo.setCreateOp(String.valueOf(photoUpload.getMemNo()));

            int i = 0;
            //如果没有传入serialNumber则是新增图片，如果传入了替换图片
            if (StringUtils.isNotEmpty(serialNumber) ) {
                logger.info("替换图片：{},orderNo:{},userType:{}",serialNumber,orderNo,userType);
                RenterOrderWzIllegalPhotoEntity illegalPhoto  =  renterOrderWzIllegalPhotoService.getIllegalPhotoBy(orderNo,Integer.parseInt(userType),Integer.parseInt(serialNumber),carNum);
                if (illegalPhoto!=null) {
                    //保证图片存储于数据库一致
                    ossService.deleteOSSObject(illegalPhoto.getPath());
                    photo.setSerialNumber(Integer.parseInt(serialNumber));
                    i = renterOrderWzIllegalPhotoService.update(photo);
                }else{
                    if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                        if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                            return -2;
                        }
                    }
                    photo.setSerialNumber(Integer.parseInt(serialNumber));
                    i = renterOrderWzIllegalPhotoService.insert(photo);
                }
            }else{
                if (Integer.parseInt(photoUpload.getUserType()) != 3) {
                    if (renterOrderWzIllegalPhotoService.countIllegalPhoto(orderNo,Integer.parseInt(photoUpload.getUserType()),carNum) >= IMAGE_UPLOAD_LIMIT) {
                        return -2;
                    }
                }
                Integer num = renterOrderWzIllegalPhotoService.getMaxSerialNum(orderNo,Integer.parseInt(userType),carNum);
                photo.setSerialNumber((num!=null?num:0)+1);
                i = renterOrderWzIllegalPhotoService.insert(photo);
            }
            if (i>0) {
                result = SUCCESS_STATUS;
                logger.info("记录文件到数据成功。。orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
                //保存成功，发送上传图片给仁云
                transIllegalSendAliYunMq.transIllegalPhotoToRenyun(photo);
            }else {
                //保证图片存储于数据库一致
                ossService.deleteOSSObject(key);
                result = FAILED_STATUS;
            }
        }else{
            logger.warn("上传文件失败orderNo : {},serialNumber:{},userType:{}",orderNo,serialNumber,userType);
        }
        return result;

    }

    private Integer validateOrderInfo(Integer memNo, String orderNo, int userType) {
        String renterNo = transMapper.getOrderInfoByOrderNo(orderNo);
        String ownerNo = transMapper.getOrderInfoByOrderNo(orderNo);
        if (StringUtils.isBlank(renterNo) && StringUtils.isBlank(ownerNo)) {
            return -3;
        }else {
            String memNoStr = String.valueOf(memNo);
            if (!memNoStr.equals(renterNo) && !memNoStr.equals(ownerNo)) {
                return -4;
            }
            if (userType == 1 && !memNoStr.equals(renterNo)) {
                //租客
                return -4;
            }
            if (userType == 2 && !memNoStr.equals(ownerNo)) {
                //车主
                return -4;
            }
        }
        return SUCCESS_STATUS;
    }

}
