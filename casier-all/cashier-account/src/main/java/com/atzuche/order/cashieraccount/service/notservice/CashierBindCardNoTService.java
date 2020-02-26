package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.cashieraccount.entity.CashierBindCardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.mapper.CashierBindCardMapper;

import java.util.Objects;


/**
 * 个人免押绑卡信息表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierBindCardNoTService {
    @Autowired
    private CashierBindCardMapper cashierBindCardMapper;

    public String test(){
        CashierBindCardEntity result = cashierBindCardMapper.selectByPrimaryKey(1);
        return Objects.nonNull(result)?result.getName():"";
    }


}
