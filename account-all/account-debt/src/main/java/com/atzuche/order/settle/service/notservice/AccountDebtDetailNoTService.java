package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.settle.exception.AccountDeductDebtDBException;
import com.atzuche.order.settle.exception.AccountInsertDebtDBException;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.mapper.AccountDebtDetailMapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 个人历史欠款明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtDetailNoTService {
    @Autowired
    private AccountDebtDetailMapper accountDebtDetailMapper;


    /**
     * 查询待抵扣历史欠款
     * @return
     */
    public List<AccountDebtDetailEntity> getDebtListByMemNo(String memNo){
        List<AccountDebtDetailEntity> result = accountDebtDetailMapper.getDebtListByMemNo(memNo);
        if(CollectionUtils.isEmpty(result)){
            throw new AccountDeductDebtDBException();
        }
        return result;
    }

    public void updateAlreadyDeductDebt(List<AccountDebtDetailEntity> accountDebtDetails) {
       for(int i=0;i<accountDebtDetails.size();i++){
          int result = accountDebtDetailMapper.updateByPrimaryKeySelective(accountDebtDetails.get(i));
          if(result==0){
              throw new AccountDeductDebtDBException();
          }
       }
    }


    public int insertDebtDetail(AccountInsertDebtReqVO accountInsertDebt) {
        AccountDebtDetailEntity accountDebtDetail = new AccountDebtDetailEntity();
        BeanUtils.copyProperties(accountInsertDebt,accountDebtDetail);
        accountDebtDetail.setCurrentDebtAmt(accountInsertDebt.getAmt());
        accountDebtDetail.setOrderDebtAmt(accountInsertDebt.getAmt());
        accountDebtDetail.setRepaidDebtAmt(NumberUtils.INTEGER_ZERO);
        int result = accountDebtDetailMapper.insert(accountDebtDetail);
        if(result==0){
            throw new AccountInsertDebtDBException();
        }
        return accountDebtDetail.getId();
    }

    /**
     * 根据租客抵扣总金额 过滤待还 欠款记录
     * @param accountDebtDetailAlls  个人所有欠款
     * @param accountDeductDebt  本次租客 要还欠款总金额 和更新人
     */
    public  List<AccountDebtDetailEntity> getDebtListByDebtAll(List<AccountDebtDetailEntity> accountDebtDetailAlls, AccountDeductDebtReqVO accountDeductDebt) {
        int amt = accountDeductDebt.getAmt();
        Integer realAmt = NumberUtils.INTEGER_ZERO;
        List<AccountDebtDetailEntity>  accountDebtDetailTodos = new ArrayList<>();
        for(int i =0;i<accountDebtDetailAlls.size();i++){
            AccountDebtDetailEntity accountDebtDetailAll = accountDebtDetailAlls.get(i);
            accountDebtDetailAll.setUpdateOp(accountDeductDebt.getUpdateOp());
            //  amt >0 表示 租客还款总金额 还有剩余   =0表示欠款记录欠款金额 刚好抵扣完  <0 表示最后一条 欠款信息 存在部分还款
            amt = amt - Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
            if(amt>0){
                accountDebtDetailAll.setCurrentDebtAmt(NumberUtils.INTEGER_ZERO);
                accountDebtDetailAll.setRepaidDebtAmt(Math.abs(accountDebtDetailAll.getOrderDebtAmt()));
                accountDebtDetailTodos.add(accountDebtDetailAll);
                realAmt = realAmt + Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
            }
            if(amt==0){
                accountDebtDetailAll.setCurrentDebtAmt(NumberUtils.INTEGER_ZERO);
                accountDebtDetailAll.setRepaidDebtAmt(Math.abs(accountDebtDetailAll.getOrderDebtAmt()));
                accountDebtDetailTodos.add(accountDebtDetailAll);
                realAmt = realAmt + Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
                break;
            }
            if(amt<0){
                accountDebtDetailAll.setCurrentDebtAmt(-Math.abs(amt));
                accountDebtDetailAll.setRepaidDebtAmt(accountDebtDetailAll.getCurrentDebtAmt()-accountDebtDetailAll.getOrderDebtAmt());
                accountDebtDetailTodos.add(accountDebtDetailAll);
                realAmt = realAmt + Math.abs(accountDebtDetailAll.getCurrentDebtAmt()) - Math.abs(amt);
                break;
            }
        }
        accountDeductDebt.setRealAmt(realAmt);
        return accountDebtDetailTodos;
    }
}
