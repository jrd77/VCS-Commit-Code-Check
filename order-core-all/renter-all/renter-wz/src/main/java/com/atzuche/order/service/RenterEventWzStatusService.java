package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterEventWzStatusMapper;
import com.atzuche.order.entity.RenterEventWzStatusEntity;



/**
 * 租客端违章处理状态表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 */
@Service
public class RenterEventWzStatusService{
    @Autowired
    private RenterEventWzStatusMapper renterEventWzStatusMapper;


}
