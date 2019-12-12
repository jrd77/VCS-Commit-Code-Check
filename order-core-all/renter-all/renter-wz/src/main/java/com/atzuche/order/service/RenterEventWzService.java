package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterEventWzMapper;
import com.atzuche.order.entity.RenterEventWzEntity;



/**
 * 租客端违章处理事件表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 */
@Service
public class RenterEventWzService{
    @Autowired
    private RenterEventWzMapper renterEventWzMapper;


}
