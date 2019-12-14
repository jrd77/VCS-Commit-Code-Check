package com.atzuche.order.rentermem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.rentermem.mapper.RenterMemberMapper;


/**
 * 租客端会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Service
public class RenterMemberService{
    @Autowired
    private RenterMemberMapper renterMemberMapper;


}
