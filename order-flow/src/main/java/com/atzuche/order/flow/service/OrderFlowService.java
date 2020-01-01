package com.atzuche.order.flow.service;

import com.atzuche.order.flow.mapper.OrderFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 租客端交易流程表
 *
 * @author ZhangBin
 * @date 2020-01-01 15:10:51
 */
@Service
public class OrderFlowService {

    @Autowired
    private OrderFlowMapper orderFlowMapper;


}
