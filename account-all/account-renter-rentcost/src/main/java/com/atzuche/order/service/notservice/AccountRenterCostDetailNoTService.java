package com.atzuche.order.service.notservice;

import com.atzuche.order.exception.AccountRenterRentCostException;
import com.atzuche.order.vo.req.AccountRenterCostDetailReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterCostDetailMapper;
import com.atzuche.order.entity.AccountRenterCostDetailEntity;

import java.time.LocalDateTime;


/**
 * 租车费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostDetailNoTService {
    @Autowired
    private AccountRenterCostDetailMapper accountRenterCostDetailMapper;


    /**
     * 费用明细落库
     * @param accountRenterCostDetailReqVO
     */
    public void insertAccountRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetailReqVO) {
        AccountRenterCostDetailEntity accountRenterCostDetail = new AccountRenterCostDetailEntity();
        BeanUtils.copyProperties(accountRenterCostDetailReqVO,accountRenterCostDetail);
        LocalDateTime now = LocalDateTime.now();
        accountRenterCostDetail.setCreateTime(now);
        accountRenterCostDetail.setUpdateTime(now);
        accountRenterCostDetail.setIsDelete(NumberUtils.INTEGER_ZERO);
        int result = accountRenterCostDetailMapper.insert(accountRenterCostDetail);
        if(result==0){
            throw new AccountRenterRentCostException(ErrorCode.FAILED);
        }
    }
}
