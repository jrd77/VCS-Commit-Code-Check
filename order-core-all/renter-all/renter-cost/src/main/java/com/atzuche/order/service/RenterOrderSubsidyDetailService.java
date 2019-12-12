package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderSubsidyDetailMapper;
import com.atzuche.order.entity.RenterOrderSubsidyDetailEntity;



/**
 * 租客补贴明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Service
public class RenterOrderSubsidyDetailService{
    @Autowired
    private RenterOrderSubsidyDetailMapper renterOrderSubsidyDetailMapper;


}
