package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.OwnerAgreeTypeEnum;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 车主同意接单
 *
 * @author pengcheng.fu
 * @date 2020/1/9 16:56
 */
@Service
public class OwnerAgreeOrderService {

    private static Logger logger = LoggerFactory.getLogger(OwnerAgreeOrderService.class);

    @Autowired
    RefuseOrderCheckService refuseOrderCheckService;

    @Autowired
    OrderStatusService orderStatusService;

    @Autowired
    RenterOrderService renterOrderService;

    @Autowired
    OrderFlowService orderFlowService;


    /**
     * 车主同意接单
     *
     * @param reqVO 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void agree(AgreeOrderReqVO reqVO) {
        //车主同意前置校验
        refuseOrderCheckService.checkOwnerAgreeOrRefuseOrder(reqVO.getOrderNo(), StringUtils.isNotBlank(reqVO.getOperatorName()));
        //变更订单状态
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(reqVO.getOrderNo());
        orderStatusDTO.setStatus(OrderStatusEnum.TO_PAY.getStatus());
        orderStatusDTO.setUpdateOp(reqVO.getOperatorName());
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);

        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(), OrderStatusEnum.TO_PAY);
        //更新租客订单车主同意信息
        RenterOrderEntity renterOrderEntity =
                renterOrderService.getRenterOrderByOrderNoAndIsEffective(reqVO.getOrderNo());

        RenterOrderEntity record = new RenterOrderEntity();
        record.setId(renterOrderEntity.getId());
        record.setReqAcceptTime(LocalDateTime.now());
        record.setAgreeFlag(OwnerAgreeTypeEnum.ARGEE.getCode());
        renterOrderService.updateRenterOrderInfo(record);

        //消息发送
        //TODO:发送车主同意事件
    }




}
