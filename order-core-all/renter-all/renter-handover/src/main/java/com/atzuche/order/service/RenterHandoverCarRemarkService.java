package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterHandoverCarRemarkMapper;
import com.atzuche.order.entity.RenterHandoverCarRemarkEntity;



/**
 * 租客端交车备注表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:15:28
 */
@Service
public class RenterHandoverCarRemarkService{
    @Autowired
    private RenterHandoverCarRemarkMapper renterHandoverCarRemarkMapper;


}
