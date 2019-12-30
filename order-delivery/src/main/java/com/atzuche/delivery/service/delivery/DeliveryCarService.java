package com.atzuche.delivery.service.delivery;

import com.atzuche.delivery.common.DeliveryCarTask;
import com.atzuche.delivery.common.DeliveryErrorCode;
import com.atzuche.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.delivery.enums.UserTypeEnum;
import com.atzuche.delivery.exception.DeliveryOrderException;
import com.atzuche.delivery.mapper.RenterDeliveryAddrMapper;
import com.atzuche.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.delivery.vo.delivery.*;
import com.atzuche.order.commons.enums.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.atzuche.delivery.common.DeliveryErrorCode.DELIVERY_PARAMS_ERROR;

/**
 * @author 胡春林
 * 配送服务
 */
@Service
public class DeliveryCarService {

    @Autowired
    DeliveryCarTask deliveryCarTask;
    @Autowired
    RenterDeliveryAddrMapper deliveryAddrMapper;
    @Autowired
    RenterOrderDeliveryMapper orderDeliveryMapper;

    /**
     * 添加配送相关信息(是否下单，是否推送仁云)
     */
    public void addRenYunFlowOrderInfo(OrderDeliveryVO orderDeliveryVO) {

        if (null == orderDeliveryVO || orderDeliveryVO.getRenterDeliveryAddrDTO() == null) {
            throw new DeliveryOrderException(DELIVERY_PARAMS_ERROR);
        }
        insertDeliveryAddress(orderDeliveryVO);
        if (orderDeliveryVO.getOrderDeliveryDTO() != null && orderDeliveryVO.getOrderDeliveryDTO().getIsNotifyRenyun().intValue() == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            RenYunFlowOrderDTO renYunFlowOrder = orderDeliveryVO.getRenYunFlowOrderDTO();
            deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrder);
        }
    }

    /**
     * 更新配送订单到仁云流程系统
     */
    public void updateRenYunFlowOrderInfo(UpdateOrderDeliveryVO updateFlowOrderVO) {
        if (null == updateFlowOrderVO || updateFlowOrderVO.getRenterDeliveryAddrDTO() == null) {
            throw new DeliveryOrderException(DELIVERY_PARAMS_ERROR);
        }
        OrderDeliveryVO orderDeliveryVO = new OrderDeliveryVO();
        orderDeliveryVO.setOrderDeliveryDTO(updateFlowOrderVO.getOrderDeliveryDTO());
        orderDeliveryVO.setRenterDeliveryAddrDTO(updateFlowOrderVO.getRenterDeliveryAddrDTO());
        insertDeliveryAddress(orderDeliveryVO);
        deliveryCarTask.updateRenYunFlowOrderInfo(updateFlowOrderVO.getUpdateFlowOrderDTO());
    }

    /**
     * 取消配送订单到仁云流程系统
     */
    public void cancelRenYunFlowOrderInfo(CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        if (null == cancelOrderDeliveryVO || cancelOrderDeliveryVO.getCancelFlowOrderDTO() == null || StringUtils.isBlank(cancelOrderDeliveryVO.getRenterOrderNo())) {
            throw new DeliveryOrderException(DELIVERY_PARAMS_ERROR);
        }
        cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo());
        deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertDeliveryAddress(OrderDeliveryVO orderDeliveryVO) {
        RenterDeliveryAddrEntity deliveryAddrEntity = new RenterDeliveryAddrEntity();
        BeanUtils.copyProperties(orderDeliveryVO.getRenterDeliveryAddrDTO(), deliveryAddrEntity);
        deliveryAddrMapper.insertSelective(deliveryAddrEntity);
        if (orderDeliveryVO.getOrderDeliveryDTO() != null) {
            RenterOrderDeliveryEntity orderDeliveryEntity = new RenterOrderDeliveryEntity();
            BeanUtils.copyProperties(orderDeliveryVO.getOrderDeliveryDTO(), orderDeliveryEntity);
            orderDeliveryMapper.insertSelective(orderDeliveryEntity);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderDelivery(String renterOrderNo) {
        RenterOrderDeliveryEntity orderDeliveryEntity = orderDeliveryMapper.findRenterOrderByRenterOrderNo(renterOrderNo);
        if (null == orderDeliveryEntity) {
            throw new DeliveryOrderException(DELIVERY_PARAMS_ERROR.getValue(), "没有找到该配送订单信息");
        }
        orderDeliveryMapper.updateById(orderDeliveryEntity.getId());
    }

}
