package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterEventClaimStatusMapper;
import com.atzuche.order.entity.RenterEventClaimStatusEntity;



/**
 * 租客端理赔处理状态表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:50:12
 */
@Service
public class RenterEventClaimStatusService{
    @Autowired
    private RenterEventClaimStatusMapper renterEventClaimStatusMapper;


}
