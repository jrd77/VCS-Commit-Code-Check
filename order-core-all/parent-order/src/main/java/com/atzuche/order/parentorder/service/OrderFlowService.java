package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.mapper.OrderFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 租客端交易流程表
 *
 * @author ZhangBin
 * @date 2019-12-30 11:16:42
 */
@Service
public class OrderFlowService{
    @Autowired
    private OrderFlowMapper orderFlowMapper;


}
