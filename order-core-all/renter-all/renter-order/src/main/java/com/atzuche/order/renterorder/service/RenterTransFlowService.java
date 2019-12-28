package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.mapper.RenterTransFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




/**
 * 租客端交易流程表
 *
 * @author ZhangBin
 * @date 2019-12-28 15:46:44
 */
@Service
public class RenterTransFlowService{
    @Autowired
    private RenterTransFlowMapper renterTransFlowMapper;


}
