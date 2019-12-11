package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.mapper.CashierBindCardMapper;


/**
 * 个人免押绑卡信息表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierBindCardService {
    @Autowired
    private CashierBindCardMapper cashierBindCardMapper;

    public String test(){
        return  "www";
    }


}
