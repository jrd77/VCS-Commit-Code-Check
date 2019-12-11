package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountPlatformSubsidyDetailMapper;
import com.atzuche.order.entity.AccountPlatformSubsidyDetailEntity;



/**
 * 平台结算补贴明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Service
public class AccountPlatformSubsidyDetailService{
    @Autowired
    private AccountPlatformSubsidyDetailMapper accountPlatformSubsidyDetailMapper;


}
