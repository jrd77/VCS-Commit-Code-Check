package com.atzuche.violation.service;


import com.atzuche.violation.entity.AccountRenterWzDepositEntity;
import com.atzuche.violation.mapper.AccountRenterWzDepositMapper;
import com.atzuche.violation.vo.req.ViolationHandleAlterationRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 违章押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositNoTService {
    @Autowired
    private AccountRenterWzDepositMapper accountRenterWzDepositMapper;




    /**
     * 查询违章信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getAccountRenterWZDepositAmt(String orderNo, String memNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        if(Objects.isNull(accountRenterDepositEntity) || Objects.isNull(accountRenterDepositEntity.getShishouDeposit())){
            return 0;
        }
        return accountRenterDepositEntity.getShishouDeposit();
    }

    /**
     * 查询违章信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public AccountRenterWzDepositEntity getAccountRenterWZDeposit(String orderNo, String memNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        return accountRenterDepositEntity;
    }
    /**
     * 查询违章信息
     * @param orderNo
     * @return
     */
    public AccountRenterWzDepositEntity getAccountRenterWZDepositByOrder(String orderNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrder(orderNo);
        return accountRenterDepositEntity;
    }

}
