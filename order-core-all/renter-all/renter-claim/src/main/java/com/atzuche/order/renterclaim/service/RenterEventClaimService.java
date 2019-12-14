package com.atzuche.order.renterclaim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.renterclaim.mapper.RenterEventClaimMapper;


/**
 * 租客端理赔处理事件表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:50:12
 */
@Service
public class RenterEventClaimService{
    @Autowired
    private RenterEventClaimMapper renterEventClaimMapper;


}
