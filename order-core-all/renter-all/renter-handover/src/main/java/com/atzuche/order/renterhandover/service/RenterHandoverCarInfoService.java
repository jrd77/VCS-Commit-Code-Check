package com.atzuche.order.renterhandover.service;

import com.atzuche.order.renterhandover.mapper.RenterHandoverCarInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客交车车信息表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:37:37
 */
@Service
public class RenterHandoverCarInfoService{
    @Autowired
    private RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;


}
