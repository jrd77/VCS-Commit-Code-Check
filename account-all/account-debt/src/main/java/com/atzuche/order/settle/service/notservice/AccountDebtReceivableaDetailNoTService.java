package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.settle.exception.AccountDeductDebtDBException;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.settle.mapper.AccountDebtReceivableaDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 个人历史欠款收款记录
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtReceivableaDetailNoTService {
    @Autowired
    private AccountDebtReceivableaDetailMapper accountDebtReceivableaDetailMapper;


    public void insertAlreadyReceivablea(List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetails) {
        for(int i=0;i<accountDebtReceivableaDetails.size();i++){
            AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail =  accountDebtReceivableaDetails.get(i);
            int result = accountDebtReceivableaDetailMapper.insertSelective(accountDebtReceivableaDetail);
            if(result==0){
                throw new AccountDeductDebtDBException();
            }
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/2 14:52
     * @Description: 通过订单号和会员号查询已经抵扣的历史欠款
     *
     **/
    public List<AccountDebtReceivableaDetailEntity> getByOrderNoAndMemNo(String orderNo, String memeNo){
        return accountDebtReceivableaDetailMapper.getByOrderNoAndMemNo(orderNo,memeNo);
    }
}
