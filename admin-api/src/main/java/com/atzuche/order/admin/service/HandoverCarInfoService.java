package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.delivery.CarConditionPhotoUploadVO;
import com.atzuche.order.admin.vo.req.delivery.DeliveryReqDTO;
import com.atzuche.order.admin.vo.req.delivery.DeliveryReqVO;
import com.atzuche.order.admin.vo.req.handover.HandoverCarInfoReqDTO;
import com.atzuche.order.admin.vo.req.handover.HandoverCarInfoReqVO;
import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.OSSUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.serialNumber;

/**
 * @author 胡春林
 * 交接车信息
 */
@Service
public class HandoverCarInfoService {

    protected  final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 上传交接车
     * @param photoUploadReqVo
     * @return
     */
    public Boolean uploadByOrderNo(CarConditionPhotoUploadVO photoUploadReqVo) throws Exception {
        logger.debug("上传图片参数：{}", ToStringBuilder.reflectionToString(photoUploadReqVo));
        String orderNo = photoUploadReqVo.getOrderNo();
        String basePath = CommonUtils.createTransBasePath(orderNo + "");
        String str = UUID.randomUUID().toString();
        String key = basePath + "delivery/" + str + ".jpg";
        int photoType = Integer.parseInt(photoUploadReqVo.getPhotoType());
        int userType = Integer.parseInt(photoUploadReqVo.getUserType());
        handoverCarService.validateOrderInfo(photoUploadReqVo.getMemNo(), orderNo, userType, photoType);
        logger.info("上传交接车。。。。。orderNo is {},photoType is{},userType is {},serialNumber is {} picKey={}", orderNo, photoType, userType, serialNumber, key);
        //生成原图片
        boolean result = OSSUtils.uploadAuth(key, photoUploadReqVo.getPhotoContent(), "");
        if (result) {
            handoverCarService.findUpdateHandoverCarInfo(orderNo, userType, photoType, key);
            logger.info("上传文件到阿里云成功。。。。。orderNo is {},photoType is{},userType is {},serialNumber is {}", orderNo, photoType, userType, serialNumber);
            return true;
        }
        return false;
    }

    /**
     * 更新交接车信息
     * @param handoverCarReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateHandoverCarInfo(HandoverCarInfoReqVO handoverCarReqVO) throws Exception {
        logger.debug("参数：{}", ToStringBuilder.reflectionToString(handoverCarReqVO));
        if (Objects.isNull(handoverCarReqVO)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "参数错误");
        }
        //车主取还车
        if (handoverCarReqVO.getOwnerHandoverCarDTO() != null) {
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = handoverCarReqVO.getOwnerHandoverCarDTO();
            //查找车主交接车相关信息
            OwnerHandoverCarInfoEntity ownerHandoverCarReturnInfoEntity = handoverCarService.getOwnerHandoverCarInfo(handoverCarInfoReqDTO.getOrderNo(), HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue());
            ownerHandoverCarReturnInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterReturnOil()));
            ownerHandoverCarReturnInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnKM()));
            handoverCarService.updateOwnerHandoverInfo(ownerHandoverCarReturnInfoEntity);
            OwnerHandoverCarInfoEntity ownerHandoverCarGetInfoEntity = handoverCarService.getOwnerHandoverCarInfo(handoverCarInfoReqDTO.getOrderNo(), HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue());
            ownerHandoverCarGetInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnOil()));
            ownerHandoverCarGetInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterRetrunKM()));
            handoverCarService.updateOwnerHandoverInfo(ownerHandoverCarGetInfoEntity);
        }
        if(handoverCarReqVO.getRenterHandoverCarDTO() != null)
        {
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = handoverCarReqVO.getRenterHandoverCarDTO();
            //查找车主交接车相关信息
            RenterHandoverCarInfoEntity renterHandoverCarReturnInfoEntity = handoverCarService.getRenterHandoverCarInfo(handoverCarInfoReqDTO.getOrderNo(), HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue());
            renterHandoverCarReturnInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterReturnOil()));
            renterHandoverCarReturnInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnKM()));
            handoverCarService.updateRenterHandoverInfo(renterHandoverCarReturnInfoEntity);
            RenterHandoverCarInfoEntity renterHandoverCarGetInfoEntity = handoverCarService.getRenterHandoverCarInfo(handoverCarInfoReqDTO.getOrderNo(), HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue());
            renterHandoverCarGetInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnOil()));
            renterHandoverCarGetInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterRetrunKM()));
            handoverCarService.updateRenterHandoverInfo(renterHandoverCarGetInfoEntity);
        }
    }

    /**
     * 更新取还车信息 更新仁云接口
     * @param handoverCarReqVO
     * @throws Exception
     */
    public void updateDeliveryCarInfo(DeliveryReqVO handoverCarReqVO) throws Exception {
        logger.debug("参数：{}", ToStringBuilder.reflectionToString(handoverCarReqVO));

    }

}
