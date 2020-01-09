package com.atzuche.order.accountrenterstopcost.service.notservice;

import com.atzuche.order.accountrenterstopcost.entity.AccountRenterStopCostDetailEntity;
import com.atzuche.order.accountrenterstopcost.exception.AccountRenterStopDetailException;
import com.atzuche.order.accountrenterstopcost.vo.req.AccountRenterStopCostDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterstopcost.mapper.AccountRenterStopCostDetailMapper;


/**
 * 停运费资金进出明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostDetailNoTService {
    @Autowired
    private AccountRenterStopCostDetailMapper accountRenterStopCostDetailMapper;


    public void insertCostDetail(AccountRenterStopCostDetailReqVO accountRenterStopCostDetail) {
        AccountRenterStopCostDetailEntity entity = new AccountRenterStopCostDetailEntity();
        BeanUtils.copyProperties(accountRenterStopCostDetail,entity);
        entity.setSourceCode(Integer.parseInt(accountRenterStopCostDetail.getRenterCashCodeEnum().getCashNo()));
        entity.setSourceDetail(accountRenterStopCostDetail.getRenterCashCodeEnum().getTxt());
        int result = accountRenterStopCostDetailMapper.insertSelective(entity);
        if(result==0){
            throw new AccountRenterStopDetailException();
        }
    }
}
