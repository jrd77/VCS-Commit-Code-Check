package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderStatus;
import com.atzuche.order.parentorder.dto.ParentOrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 主订单相关信息处理类
 *
 *
 * @author pengcheng.fu
 * @date 2019/12/24 17:34
 */
public class ParentOrderService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ParentOrderService.class);


    @Resource
    OrderService orderService;

    @Resource
    OrderStatusService orderStatusService;

    @Resource
    OrderSourceStatService orderSourceStatService;


    public void saveParentOrderInfo(ParentOrderDTO parentOrderDTO) {
        LOGGER.info("Save parent order info ,param is, parentOrderDTO:[{}]", JSON.toJSONString(parentOrderDTO));

        //1、主订单信息处理
        int saveOrderResult = orderService.saveOrderInfo(parentOrderDTO.getOrderDTO());

        //2、主订单状态信息处理
        int saveOrderStatusResult =  orderStatusService.saveOrderStatusInfo(parentOrderDTO.getOrderStatusDTO());

        //3、主订单来源统计信息处理
        int saveOrderSourceStatResult = orderSourceStatService.saveOrderSourceStatInfo(parentOrderDTO.getOrderSourceStatDTO());
    }


}
