package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderChangeApplyMapper;
import com.atzuche.order.entity.RenterOrderChangeApplyEntity;



/**
 * 租客订单变更申请表
 *
 * @author ZhangBin
 * @date 2019-12-12 14:29:22
 */
@Service
public class RenterOrderChangeApplyService{
    @Autowired
    private RenterOrderChangeApplyMapper renterOrderChangeApplyMapper;


}
