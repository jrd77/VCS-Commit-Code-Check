package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.DeliveryTypeEnum;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.RenterHandoverCarRemarkMapper;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.service.delivery.RenYunDeliveryCarService;
import com.atzuche.order.delivery.utils.OSSUtils;
import com.atzuche.order.delivery.vo.delivery.*;
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

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    DeliveryCarService deliveryCarInfoService;
    @Resource
    RenterOrderDeliveryMapper renterOrderDeliveryMapper;
    @Autowired
    DeliveryCarTask deliveryCarTask;
    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;

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
     *
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
            //更新车主交接车相关信息
            ownerHandoverCarService.updateHandoverCarOilMileageNum(handoverCarInfoReqDTO);
        }
        if (handoverCarReqVO.getRenterHandoverCarDTO() != null) {
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = handoverCarReqVO.getRenterHandoverCarDTO();
            //更新租客交接车相关信息
            renterHandoverCarService.updateHandoverCarOilMileageNum(handoverCarInfoReqDTO);
        }
    }

    /**
     * 更新取还车信息 更新仁云接口
     *
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
        if (deliveryReqVO.getGetDeliveryReqDTO() != null) {
            deliveryReqDTO = deliveryReqVO.getGetDeliveryReqDTO();
            updateDeliveryCarInfoByUsed(deliveryReqDTO, 1);
        }
        if (deliveryReqVO.getRenterDeliveryReqDTO() != null) {
            deliveryReqDTO = deliveryReqVO.getRenterDeliveryReqDTO();
            updateDeliveryCarInfoByUsed(deliveryReqDTO, 2);
        }
    }

    /**
     * 更新配送订单相关信息
     *
     * @param deliveryReqDTO
     */
    public void updateDeliveryCarInfoByUsed(DeliveryReqDTO deliveryReqDTO, Integer type) {
        RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryMapper.findRenterOrderByrOrderNo(deliveryReqDTO.getOrderNo(), type);
        if (renterOrderDeliveryEntity != null && String.valueOf(UsedDeliveryTypeEnum.NO_USED.getValue()).equals(deliveryReqDTO.getIsUsedGetAndReturnCar())) {
            if (renterOrderDeliveryEntity.getStatus().intValue() != 3 && renterOrderDeliveryEntity.getStatus().intValue() != 0) {
                deliveryCarInfoService.cancelRenYunFlowOrderInfo(new CancelOrderDeliveryVO().setCancelFlowOrderDTO(new CancelFlowOrderDTO().setServicetype(type == 1 ? "take" : "back").setOrdernumber(renterOrderDeliveryEntity.getOrderNo())).setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo()));
            }
        } else if (renterOrderDeliveryEntity != null && String.valueOf(UsedDeliveryTypeEnum.USED.getValue()).equals(deliveryReqDTO.getIsUsedGetAndReturnCar())) {
            UpdateOrderDeliveryVO updateOrderDeliveryVO = createDeliveryCarInfoParams(renterOrderDeliveryEntity, deliveryReqDTO, type);
            deliveryCarInfoService.updateFlowOrderInfo(updateOrderDeliveryVO);
        } else {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有合适的参数");
        }
    }

    /**
     * 构造更新参数
     *
     * @return
     */
    public UpdateOrderDeliveryVO createDeliveryCarInfoParams(RenterOrderDeliveryEntity renterOrderDeliveryEntity, DeliveryReqDTO deliveryReqDTO, Integer type) {
        OrderDeliveryDTO orderDeliveryDTO = new OrderDeliveryDTO();
        UpdateFlowOrderDTO updateFlowOrderDTO = new UpdateFlowOrderDTO();
        RenterDeliveryAddrDTO renterDeliveryAddrDTO = new RenterDeliveryAddrDTO();
        orderDeliveryDTO.setType(type);
        orderDeliveryDTO.setRenterGetReturnAddr(deliveryReqDTO.getRenterRealGetAddr());
        orderDeliveryDTO.setOwnerGetReturnAddr(deliveryReqDTO.getOwnRealReturnAddr());
        orderDeliveryDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        orderDeliveryDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        renterDeliveryAddrDTO.setExpGetCarAddr(deliveryReqDTO.getRenterRealGetAddr());
        renterDeliveryAddrDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        renterDeliveryAddrDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        renterDeliveryAddrDTO.setExpReturnCarAddr(deliveryReqDTO.getOwnRealReturnAddr());
        renterDeliveryAddrDTO.setActReturnCarAddr(deliveryReqDTO.getOwnRealReturnAddr());
        renterDeliveryAddrDTO.setActGetCarAddr(deliveryReqDTO.getRenterRealGetAddr());
        updateFlowOrderDTO.setOrdernumber(deliveryReqDTO.getOrderNo());
        updateFlowOrderDTO.setServicetype(type == 1 ? "take" : "back");
        updateFlowOrderDTO.setChangetype("ownerAddr");
        updateFlowOrderDTO.setNewpickupcaraddr(deliveryReqDTO.getRenterRealGetAddr());
        updateFlowOrderDTO.setNewalsocaraddr(deliveryReqDTO.getOwnRealReturnAddr());
        return UpdateOrderDeliveryVO.builder().orderDeliveryDTO(orderDeliveryDTO).renterDeliveryAddrDTO(renterDeliveryAddrDTO).updateFlowOrderDTO(updateFlowOrderDTO).build();
    }

}
