package com.atzuche.order.flow.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.dto.OrderFlowDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.atzuche.order.flow.mapper.OrderFlowMapper;
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
                orderFlowDTO.setCreateTime(DateUtils.formate(orderFlowEntity.getCreateTime(),DateUtils.DATE_DEFAUTE1));
                orderFlowDTOList.add(orderFlowDTO);
            });
        }
        orderFlowListResponseVO.setOrderFlowList(orderFlowDTOList);
        LOGGER.info("orderFlow list result is: [{}]", orderFlowList);
        return orderFlowListResponseVO;
    }
    /*
     * @Author ZhangBin
     * @Date 2020/1/16 19:37
     * @Description: 通过订单号获取流程状态
     *
     **/
    public OrderFlowEntity getByOrderNoAndStatus(String oderNo,Integer orderStatus){
        return orderFlowMapper.getByOrderNoAndStatus(oderNo,orderStatus);
    }

}
