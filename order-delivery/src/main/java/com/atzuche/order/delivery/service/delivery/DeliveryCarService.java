package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.mapper.RenterDeliveryAddrMapper;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.OrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateOrderDeliveryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 胡春林
 * 配送服务
 */
@Service
public class DeliveryCarService {

    @Autowired
    DeliveryCarTask deliveryCarTask;
    @Resource
    RenterDeliveryAddrMapper deliveryAddrMapper;
    @Resource
    RenterOrderDeliveryMapper orderDeliveryMapper;

    /**
     * 添加配送相关信息(是否下单，是否推送仁云)
     */
    public void addRenYunFlowOrderInfo(OrderDeliveryVO orderDeliveryVO) {

        if (null == orderDeliveryVO || orderDeliveryVO.getRenterDeliveryAddrDTO() == null) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
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
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
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
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int serviceType = cancelOrderDeliveryVO.getCancelFlowOrderDTO().getServicetype().equals(ServiceTypeEnum.TAKE_TYPE.getValue()) ? 1 : 2;
        cancelOrderDelivery(cancelOrderDeliveryVO.getRenterOrderNo(),serviceType);
        deliveryCarTask.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
    }

    /**
     * 插入配送地址/配送订单信息
     * @param orderDeliveryVO
     */
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

    /**
     * 取消配送订单
     * @param renterOrderNo
     * @param serviceType
     */
    public void cancelOrderDelivery(String renterOrderNo,Integer serviceType) {
        RenterOrderDeliveryEntity orderDeliveryEntity = orderDeliveryMapper.findRenterOrderByRenterOrderNo(renterOrderNo,serviceType);
        if (null == orderDeliveryEntity) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到该配送订单信息");
        }
        orderDeliveryMapper.updateById(orderDeliveryEntity.getId());
    }

}
