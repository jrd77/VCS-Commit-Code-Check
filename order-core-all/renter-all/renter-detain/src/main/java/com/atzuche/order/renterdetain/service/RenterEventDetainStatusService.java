package com.atzuche.order.renterdetain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.renterdetain.mapper.RenterEventDetainStatusMapper;


/**
 * 租客端暂扣处理状态表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Service
public class RenterEventDetainStatusService{
    @Autowired
    private RenterEventDetainStatusMapper renterEventDetainStatusMapper;


}
