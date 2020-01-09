package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.*;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.handover.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 * 交接车服务
 */
@Service
@Slf4j
public class HandoverCarService {

    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;

    /**
     * 新增交接车数据
     *
     * @param handoverCarVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addHandoverCarInfo(HandoverCarVO handoverCarVO, int userType) {
        if (Objects.isNull(handoverCarVO) || handoverCarVO.getHandoverCarInfoDTO().getType() == null) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
            insertOrUpdateRenterHandoverCarInfo(handoverCarVO.getHandoverCarInfoDTO(), handoverCarVO.getHandoverCarRemarkDTO());
        } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
            insertOrUpdateOwnerHandoverCarInfo(handoverCarVO.getHandoverCarInfoDTO(), handoverCarVO.getHandoverCarRemarkDTO());
        } else {
            log.info("没有找到合适的交车类型，handoverCarVO:{}", handoverCarVO.toString());
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到合适的交车类型");
        }
    }

    /**
     * 根据消息ID获取
     *
     * @param msgId
     * @return
     */
    public String getHandoverCarInfoByMsgId(String msgId) {
        if (StringUtils.isBlank(msgId)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        String handoverMsgId = renterHandoverCarService.queryObjectByMsgId(msgId);
        if (StringUtils.isBlank(handoverMsgId)) {
            handoverMsgId = ownerHandoverCarService.queryObjectByMsgId(msgId);
        }
        return handoverMsgId;
    }


    /**
     * 根据子订单号查询(油耗 里程)需要的数据
     *
     * @param handoverCarReqVO
     * @return
     */
    public HandoverCarRepVO getRenterHandover(HandoverCarReqVO handoverCarReqVO) {
        if (null == handoverCarReqVO) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "参数错误");
        }
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = renterHandoverCarService.selectRenterHandoverCarByOrderNo(handoverCarReqVO.getRenterOrderNo());
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = ownerHandoverCarService.selectOwnerHandoverCarByOrderNo(handoverCarReqVO.getOwnerOrderNo());
        HandoverCarRepVO handoverCarRepVO = new HandoverCarRepVO();
        handoverCarRepVO.setOwnerHandoverCarInfoEntities(ownerHandoverCarInfoEntities);
        handoverCarRepVO.setRenterHandoverCarInfoEntities(renterHandoverCarInfoEntities);
        return handoverCarRepVO;
    }

    /**
     * 更新图片数据
     *
     * @param orderNo
     * @param userType
     * @param photoType
     * @param key
     */
    public void findUpdateHandoverCarInfo(String orderNo, Integer userType, Integer photoType, String key) {
        RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = null;
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = null;
        setHandoverCarInfo(renterHandoverCarInfoEntity, ownerHandoverCarInfoEntity, userType, orderNo, photoType);
        if (renterHandoverCarInfoEntity != null) {
            renterHandoverCarInfoEntity.setImageUrl(key);
            renterHandoverCarService.updateRenterHandoverInfoByPrimaryKey(renterHandoverCarInfoEntity);
        }
        if (ownerHandoverCarInfoEntity != null) {
            ownerHandoverCarInfoEntity.setImageUrl(key);
            ownerHandoverCarService.updateOwnerHandoverInfoByPrimaryKey(ownerHandoverCarInfoEntity);
        }
    }

    /**
     * 校验订单信息
     *
     * @param memNO
     * @param orderNo
     * @param userType
     * @return
     */
    public boolean validateOrderInfo(Integer memNO, String orderNo, int userType, Integer photoType) {
        RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = null;
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = null;
        setHandoverCarInfo(renterHandoverCarInfoEntity, ownerHandoverCarInfoEntity, userType, orderNo, photoType);
        if (null == renterHandoverCarInfoEntity && Objects.isNull(ownerHandoverCarInfoEntity)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到该笔订单记录");
        }
        if (memNO.intValue() != ownerHandoverCarInfoEntity.getRealGetMemNo().intValue() && memNO.intValue() != renterHandoverCarInfoEntity.getRealGetMemNo().intValue()) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "您只能上传自己的取还车照片");
        }
        return true;
    }

    /**
     * 设置参数
     *
     * @param renterHandoverCarInfoEntity
     * @param ownerHandoverCarInfoEntity
     * @param userType
     * @param orderNo
     * @param photoType
     */
    public void setHandoverCarInfo(RenterHandoverCarInfoEntity renterHandoverCarInfoEntity, OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity, Integer userType, String orderNo, Integer photoType) {
        int type = photoType == 2 ? 3 : 4;
        if (UserTypeEnum.isUserType(userType) && userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
            renterHandoverCarInfoEntity = renterHandoverCarService.selectObjectByOrderNo(orderNo, type);
        } else if (UserTypeEnum.isUserType(userType) && userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
            ownerHandoverCarInfoEntity = ownerHandoverCarService.selectObjectByOrderNo(orderNo, type);
        } else {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到合适的类型");
        }
        if (null == renterHandoverCarInfoEntity && Objects.isNull(ownerHandoverCarInfoEntity)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到该笔订单记录");
        }
    }


    /**
     * 更新租客交接车和备注信息
     * @param handoverCarInfoDTO
     * @param handoverCarRemarkDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateRenterHandoverCarInfo(HandoverCarInfoDTO handoverCarInfoDTO, HandoverCarRemarkDTO handoverCarRemarkDTO) {
        RenterHandoverCarInfoEntity handoverCarInfoEntity = renterHandoverCarService.selectObjectByOrderNo(handoverCarInfoDTO.getOrderNo(), handoverCarInfoDTO.getType());
        RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity = renterHandoverCarService.selectRenterHandoverRemarkByOrderNoType(handoverCarInfoDTO.getOrderNo(), handoverCarInfoDTO.getType());
        if (Objects.nonNull(handoverCarInfoEntity)) {
            CommonUtil.copyPropertiesIgnoreNull(handoverCarInfoDTO, handoverCarInfoEntity);
             renterHandoverCarService.updateRenterHandoverInfoByPrimaryKey(handoverCarInfoEntity);
        } else {
            RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = new RenterHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarInfoDTO, renterHandoverCarInfoEntity);
            renterHandoverCarService.insertRenterHandoverCar(renterHandoverCarInfoEntity);
        }
        if (Objects.nonNull(handoverCarRemarkDTO)) {
            if (null == renterHandoverCarRemarkEntity) {
                RenterHandoverCarRemarkEntity renterRemarkEntity = new RenterHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarRemarkDTO, renterRemarkEntity);
                 renterHandoverCarService.insertRenterHandoverCarRemark(renterRemarkEntity);
            } else {
                CommonUtil.copyPropertiesIgnoreNull(handoverCarRemarkDTO, renterHandoverCarRemarkEntity);
                renterHandoverCarService.updateRenterHandoverRemarkByPrimaryKey(renterHandoverCarRemarkEntity);
            }
        }

    }


    /**
     * 更新车主交接车和备注信息
     * @param handoverCarInfoDTO
     * @param handoverCarRemarkDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateOwnerHandoverCarInfo(HandoverCarInfoDTO handoverCarInfoDTO, HandoverCarRemarkDTO handoverCarRemarkDTO) {
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarService.selectObjectByOrderNo(handoverCarInfoDTO.getOrderNo(), handoverCarInfoDTO.getType());
        OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity = ownerHandoverCarService.selectOwnerHandoverRemarkByOrderNoType(handoverCarInfoDTO.getOrderNo(), handoverCarInfoDTO.getType());
        if (Objects.nonNull(ownerHandoverCarInfoEntity)) {
            CommonUtil.copyPropertiesIgnoreNull(handoverCarInfoDTO, ownerHandoverCarInfoEntity);
            ownerHandoverCarService.updateOwnerHandoverInfoByPrimaryKey(ownerHandoverCarInfoEntity);
        } else {
            OwnerHandoverCarInfoEntity ownerHandoverCarInfo = new OwnerHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarInfoDTO, ownerHandoverCarInfo);
            ownerHandoverCarService.insertOwnerHandoverCarInfo(ownerHandoverCarInfo);
        }
        if (Objects.nonNull(handoverCarRemarkDTO)) {
            if (null == ownerHandoverCarRemarkEntity) {
                OwnerHandoverCarRemarkEntity ownerRemarkEntity = new OwnerHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarRemarkDTO, ownerRemarkEntity);
                ownerHandoverCarService.insertOwnerHandoverCarRemark(ownerRemarkEntity);
            } else {
                CommonUtil.copyPropertiesIgnoreNull(handoverCarRemarkDTO, ownerHandoverCarRemarkEntity);
                ownerHandoverCarService.updateOwnerHandoverRemarkByPrimaryKey(ownerHandoverCarRemarkEntity);
            }
        }
    }
}
