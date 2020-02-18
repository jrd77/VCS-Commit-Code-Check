package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.OwnerChildStatusEnum;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 车主交车
 *
 * @author pengcheng.fu
 * @date 2020/1/16 18:13
 */
@Service
public class OwnerReturnCarService {

    private static Logger logger = LoggerFactory.getLogger(OwnerReturnCarService.class);

    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    OrderFlowService orderFlowService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    RefuseOrderCheckService refuseOrderCheckService;
    @Autowired
    OrderActionMqService orderActionMqService;
    @Autowired
    OrderStatusMqService orderStatusMqService;

    @Transactional(rollbackFor = Exception.class)
    public void returnCar(ReturnCarReqVO reqVO) {

        //车主交车前置校验
        refuseOrderCheckService.checkOwnerReturnCar(reqVO.getOrderNo(), StringUtils.isNotBlank(reqVO.getOperatorName()));

        //订单状态处理
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(reqVO.getOrderNo());
        orderStatusDTO.setStatus(OrderStatusEnum.TO_SETTLE.getStatus());
        orderStatusDTO.setUpdateOp(reqVO.getOperatorName());
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(), OrderStatusEnum.TO_SETTLE);

        //更新子订单状态以及实际还车时间
        RenterOrderEntity renterOrderEntity =
                renterOrderService.getRenterOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
        if (null != renterOrderEntity) {
            RenterOrderEntity record = new RenterOrderEntity();
            record.setId(renterOrderEntity.getId());
            record.setChildStatus(RenterChildStatusEnum.FINISH.getCode());
            record.setActRevertTime(LocalDateTime.now());
            record.setUpdateOp(reqVO.getOperatorName());
            renterOrderService.updateRenterOrderInfo(record);
        }

        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
        if (null != ownerOrderEntity) {
            OwnerOrderEntity record = new OwnerOrderEntity();
            record.setId(ownerOrderEntity.getId());
            record.setChildStatus(OwnerChildStatusEnum.FINISH.getCode());
            record.setActRevertTime(LocalDateTime.now());
            record.setUpdateOp(reqVO.getOperatorName());
            ownerOrderService.updateOwnerOrderInfo(record);
        }

        //发送车主确认还车事件
        orderActionMqService.sendOrderOwnerReturnCarSuccess(reqVO.getOrderNo());

        orderStatusMqService.sendOrderStatusByOrderNo(reqVO.getOrderNo(),orderStatusDTO.getStatus(), NewOrderMQStatusEventEnum.ORDER_PRESETTLEMENT);
    }

}
