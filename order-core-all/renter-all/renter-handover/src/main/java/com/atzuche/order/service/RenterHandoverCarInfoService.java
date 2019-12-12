package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterHandoverCarInfoMapper;
import com.atzuche.order.entity.RenterHandoverCarInfoEntity;



/**
 * 租客交车车信息表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:15:28
 */
@Service
public class RenterHandoverCarInfoService{
    @Autowired
    private RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;


}
