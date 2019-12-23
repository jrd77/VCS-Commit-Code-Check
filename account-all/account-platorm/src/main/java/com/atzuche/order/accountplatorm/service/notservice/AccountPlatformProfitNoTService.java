package com.atzuche.order.accountplatorm.service.notservice;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitEntity;
import com.atzuche.order.accountplatorm.exception.AccountPlatormException;
import com.atzuche.order.accountplatorm.mapper.AccountPlatformProfitMapper;
import com.atzuche.order.accountplatorm.vo.req.AccountPlatformProfitReqVO;
import com.atzuche.order.accountplatorm.vo.res.AccountPlatformProfitResVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 *  平台订单收益结算表
 *
 * @author ZhangBin
 * @date 2019-12-21 14:38:34
 */
@Service
public class AccountPlatformProfitNoTService {
    @Autowired
    private AccountPlatformProfitMapper accountPlatformProfitMapper;


    /**
     *  查询订单结算信息
     * @param orderNo
     * @return
     */
    public AccountPlatformProfitResVO getAccountPlatformProfit(String orderNo) {
        AccountPlatformProfitResVO result = new AccountPlatformProfitResVO();
        AccountPlatformProfitEntity accountPlatformProfitEntity = accountPlatformProfitMapper.getAccountPlatformProfit(orderNo);
        if(Objects.nonNull(accountPlatformProfitEntity) && Objects.nonNull(accountPlatformProfitEntity.getId())){
            BeanUtils.copyProperties(accountPlatformProfitEntity,result);
        }
        return result;
    }

    /**
     * 订单结算信息 落库
     * @param accountPlatformProfit
     */
    public void insertAccountPlatformProfit(AccountPlatformProfitReqVO accountPlatformProfit) {
        AccountPlatformProfitEntity accountPlatformProfitEntity = new  AccountPlatformProfitEntity();
        BeanUtils.copyProperties(accountPlatformProfit,accountPlatformProfitEntity);
        int result = accountPlatformProfitMapper.insert(accountPlatformProfitEntity);
        if(result==0){
            throw new AccountPlatormException();
        }
    }
}
