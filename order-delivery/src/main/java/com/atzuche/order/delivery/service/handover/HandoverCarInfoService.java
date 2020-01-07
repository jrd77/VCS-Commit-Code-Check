package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.RenterHandoverCarRemarkMapper;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.utils.OSSUtils;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqVO;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Autowired
    DeliveryCarService deliveryCarInfoService;
    @Resource
    RenterOrderDeliveryMapper renterOrderDeliveryMapper;
    @Resource
    RenterHandoverCarRemarkMapper renterHandoverCarRemarkMapper;

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
            //查找租客交接车相关信息
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
     * @param deliveryReqVO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryCarInfo(DeliveryReqVO deliveryReqVO) throws Exception {
        logger.debug("参数：{}", ToStringBuilder.reflectionToString(deliveryReqVO));
        if (Objects.isNull(deliveryReqVO)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "参数错误");
        }
        DeliveryReqDTO deliveryReqDTO;
        //是否使用取车服务
        if(deliveryReqVO.getGetDeliveryReqDTO() != null){
            deliveryReqDTO = deliveryReqVO.getGetDeliveryReqDTO();
            RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryMapper.findRenterOrderByrOrderNo(deliveryReqDTO.getOrderNo(),1);
            //取换车(租客向平台交车，车主)
            RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity =renterHandoverCarRemarkMapper.findRemarkObjectByRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo(),4);
            //不使用取车服务
            if(deliveryReqDTO.getIsUsedGetAndReturnCar().equals("0"))
            {
                //配送订单不是取消或初始状态
                if(renterOrderDeliveryEntity.getStatus().intValue() != 3 && renterOrderDeliveryEntity.getStatus().intValue() != 0)
                {
                    //1.更新配送訂單表
                    //2.根据原先的状态发送仁云取消事件
                    renterOrderDeliveryEntity.setStatus(3);
                    //发送取消事件
                }
            }
            else if(deliveryReqDTO.getIsUsedGetAndReturnCar().equals("1"))
            {
                //1.更新配送订单表
                renterOrderDeliveryEntity.setStatus(2);
                //发送更新事件
                renterOrderDeliveryEntity.setOwnerGetReturnAddr(deliveryReqDTO.getOwnRealReturnAddr());
                renterOrderDeliveryEntity.setRenterGetReturnAddr(deliveryReqDTO.getRenterRealGetAddr());
                renterHandoverCarRemarkEntity.setRemark(deliveryReqDTO.getRenterRealGetAddrReamrk());
                //更新交接车备注数据

            }else {
                throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(),"没有合适的参数");
            }

        }
        //是否使用还车服务
        if(deliveryReqVO.getRenterDeliveryReqDTO() != null){
            deliveryReqDTO = deliveryReqVO.getRenterDeliveryReqDTO();
            //不使用取车服务
            if(deliveryReqDTO.getIsUsedGetAndReturnCar().equals("0"))
            {
                //1.更新配送訂單表
                //2.根据原先的状态发送仁云取消事件
            }

        }














    }

}
