package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.*;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarReqVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Resource
    RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;
    @Resource
    RenterHandoverCarRemarkMapper renterHandoverCarRemarkMapper;
    @Resource
    OwnerHandoverCarInfoMapper ownerHandoverCarInfoMapper;
    @Resource
    OwnerHandoverCarRemarkMapper ownerHandoverCarRemarkMapper;

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
        //向租客交车
        if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
            RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = new RenterHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarVO.getHandoverCarInfoDTO(), renterHandoverCarInfoEntity);
            //获取交接车数据
            RenterHandoverCarInfoEntity handoverCarInfoEntity = renterHandoverCarInfoMapper.selectObjectByRenterOrderNo(handoverCarVO.getHandoverCarInfoDTO().getRenterOrderNo(), handoverCarVO.getHandoverCarInfoDTO().getType());
            if (handoverCarInfoEntity != null) {
                CommonUtil.copyPropertiesIgnoreNull(renterHandoverCarInfoEntity, handoverCarInfoEntity);
                renterHandoverCarInfoMapper.updateByPrimaryKey(handoverCarInfoEntity);
            } else {
                renterHandoverCarInfoMapper.insertSelective(renterHandoverCarInfoEntity);
            }
            if (handoverCarVO.getHandoverCarRemarkDTO() != null) {
                RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity = new RenterHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarVO.getHandoverCarRemarkDTO(), renterHandoverCarRemarkEntity);
                renterHandoverCarRemarkMapper.insertSelective(renterHandoverCarRemarkEntity);
            }
        } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
            OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = new OwnerHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarVO.getHandoverCarInfoDTO(), ownerHandoverCarInfoEntity);
            ownerHandoverCarInfoMapper.insertSelective(ownerHandoverCarInfoEntity);
            if (handoverCarVO.getHandoverCarRemarkDTO() != null) {
                OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity = new OwnerHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarVO.getHandoverCarRemarkDTO(), ownerHandoverCarRemarkEntity);
                ownerHandoverCarRemarkMapper.insertSelective(ownerHandoverCarRemarkEntity);
            }
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
        String handoverMsgId = renterHandoverCarInfoMapper.queryObjectByMsgId(msgId);
        if (StringUtils.isBlank(handoverMsgId)) {
            handoverMsgId = ownerHandoverCarInfoMapper.queryObjectByMsgId(msgId);
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
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = renterHandoverCarInfoMapper.selectByRenterOrderNo(handoverCarReqVO.getRenterOrderNo());
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = ownerHandoverCarInfoMapper.selectByOwnerOrderNo(handoverCarReqVO.getRenterOrderNo());
        HandoverCarRepVO handoverCarRepVO = new HandoverCarRepVO();
        handoverCarRepVO.setOwnerHandoverCarInfoEntities(ownerHandoverCarInfoEntities);
        handoverCarRepVO.setRenterHandoverCarInfoEntities(renterHandoverCarInfoEntities);
        return handoverCarRepVO;
    }


}
