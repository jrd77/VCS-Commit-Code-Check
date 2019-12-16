package com.atzuche.order.accountownercost.service.notservice;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import com.atzuche.order.accountownercost.exception.AccountOwnerCostSettleException;
import com.atzuche.order.accountownercost.mapper.AccountOwnerCostSettleMapper;
import com.atzuche.order.accountownercost.vo.req.AccountOwnerCostSettleReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 *   车主结算费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Service
public class AccountOwnerCostSettleNoTService {
    @Autowired
    private AccountOwnerCostSettleMapper accountOwnerCostSettleMapper;


    /**
     * 车主结算信息插入
     * @param accountOwnerCostSettleReqVO
     */
    public void insertAccountOwnerCostSettle(AccountOwnerCostSettleReqVO accountOwnerCostSettleReqVO) {
        AccountOwnerCostSettleEntity accountOwnerCostSettle = new AccountOwnerCostSettleEntity();
        BeanUtils.copyProperties(accountOwnerCostSettleReqVO,accountOwnerCostSettle);
        LocalDateTime now = LocalDateTime.now();
        accountOwnerCostSettle.setCreateTime(now);
        accountOwnerCostSettle.setUpdateTime(now);
        accountOwnerCostSettle.setIsDelete(NumberUtils.INTEGER_ZERO);
        int result = accountOwnerCostSettleMapper.insert(accountOwnerCostSettle);
        if(result==0){
            throw new AccountOwnerCostSettleException();
        }
    }
}
