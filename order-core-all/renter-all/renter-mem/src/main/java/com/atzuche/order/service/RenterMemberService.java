package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterMemberMapper;
import com.atzuche.order.entity.RenterMemberEntity;



/**
 * 租客端会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:17:27
 */
@Service
public class RenterMemberService{
    @Autowired
    private RenterMemberMapper renterMemberMapper;


}
