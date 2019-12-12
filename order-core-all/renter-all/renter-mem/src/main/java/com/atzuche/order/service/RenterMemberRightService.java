package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterMemberRightMapper;
import com.atzuche.order.entity.RenterMemberRightEntity;



/**
 * 租客端会员权益表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:17:27
 */
@Service
public class RenterMemberRightService{
    @Autowired
    private RenterMemberRightMapper renterMemberRightMapper;


}
