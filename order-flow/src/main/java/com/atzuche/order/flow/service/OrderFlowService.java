package com.atzuche.order.flow.service;

import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.flow.dto.resp.OrderFlowDTO;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.atzuche.order.flow.mapper.OrderFlowMapper;
import com.atzuche.order.flow.dto.req.OrderFlowRequestDTO;
import com.atzuche.order.flow.dto.resp.OrderFlowListResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 租客端交易流程表(主订单状态变化过程记录)
 *
 * @author ZhangBin
 * @date 2020-01-01 15:10:51
 */
@Service
public class OrderFlowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFlowService.class);

    @Autowired
    private OrderFlowMapper orderFlowMapper;


    public void inserOrderStatusChangeProcessInfo(String orderNo, OrderStatusEnum orderStatus) {
        LOGGER.info("Add order status change records. param is >> orderNo:[{}],orderStatus:[{}]", orderNo, orderStatus);
        OrderFlowEntity record = new OrderFlowEntity();
        record.setOrderNo(orderNo);
        record.setOrderStatus(orderStatus.getStatus());
        record.setOrderStatusDesc(orderStatus.getDesc());
        
        int reslut = orderFlowMapper.insert(record);
        LOGGER.info("Add order status change records. result is: [{}]", reslut);
    }

    /**
     *获取订单状态流转列表
     * @param orderFlowRequestVO
     * @return
     */
    public OrderFlowListResponseDTO selectOrderFlowListByOrderNo(OrderFlowRequestDTO orderFlowRequestVO) {
        LOGGER.info("orderFlow list. param is >> [{}]", orderFlowRequestVO);
        OrderFlowListResponseDTO orderFlowListResponseVO = new OrderFlowListResponseDTO();
        List<OrderFlowDTO> orderFlowDTOList = new ArrayList();
        List<OrderFlowEntity> orderFlowList = orderFlowMapper.selectOrderFlowListByOrderNo(orderFlowRequestVO);
        if(!CollectionUtils.isEmpty(orderFlowList)) {
            orderFlowList.forEach(orderFlowEntity -> {
                OrderFlowDTO orderFlowDTO = new OrderFlowDTO();
                BeanUtils.copyProperties(orderFlowEntity ,orderFlowDTO);
                orderFlowDTO.setCreateTime(orderFlowEntity.getCreateTime());
                orderFlowDTOList.add(orderFlowDTO);
            });
        }
        orderFlowListResponseVO.setOrderFlowList(orderFlowDTOList);
        LOGGER.info("orderFlow list result is: [{}]", orderFlowList);
        return orderFlowListResponseVO;
    }


}
