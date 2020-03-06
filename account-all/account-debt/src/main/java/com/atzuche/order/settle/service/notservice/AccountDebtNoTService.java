package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.entity.AccountDebtEntity;
import com.atzuche.order.settle.exception.AccountDeductDebtDBException;
import com.atzuche.order.settle.exception.AccountInsertDebtDBException;
import com.atzuche.order.settle.mapper.AccountDebtMapper;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.res.AccountDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtNoTService {
    @Autowired
    private AccountDebtMapper accountDebtMapper;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     * @throws AccountDeductDebtDBException
     */
    public AccountDebtResVO getAccountDebtByMemNo(String memNo) {
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(memNo);
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            accountDebtEntity = new AccountDebtEntity();
            accountDebtEntity.setMemNo(memNo);
            accountDebtEntity.setDebtAmt(NumberUtils.INTEGER_ZERO);
            accountDebtEntity.setVersion(NumberUtils.INTEGER_ONE);
            accountDebtMapper.insertSelective(accountDebtEntity);
        }
        AccountDebtResVO res = new AccountDebtResVO();
        BeanUtils.copyProperties(accountDebtEntity,res);
        return res;
    }



    /**
     * 抵扣还款  欠款表记录更新
     * @param accountDeductDebt
     */
    public AccountDebtEntity deductAccountDebt(AccountDeductDebtReqVO accountDeductDebt) {
        //1 查询用户欠款总和
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountDeductDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            throw new AccountDeductDebtDBException();
        }
        int amt = accountDebtEntity.getDebtAmt()+accountDeductDebt.getRealAmt();
        if(amt>0){
            throw new AccountDeductDebtDBException();
        }
        accountDebtEntity.setDebtAmt(amt);
        int result = accountDebtMapper.updateByPrimaryKeySelective(accountDebtEntity);
        if(result==0){
            throw new AccountDeductDebtDBException();
        }
        return accountDebtEntity;
    }

    /**
     *  新产生欠款  更新总欠款
     * @param accountInsertDebt
     */
    public void productAccountDebt(AccountInsertDebtReqVO accountInsertDebt) {
        //1 查询用户欠款总和
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountInsertDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            accountDebtEntity = new AccountDebtEntity();
            accountDebtEntity.setDebtAmt(accountInsertDebt.getAmt());
            accountDebtEntity.setMemNo(accountInsertDebt.getMemNo());
            accountDebtEntity.setVersion(1);
            int result = accountDebtMapper.insertSelective(accountDebtEntity);
            if(result==0){
                throw new AccountInsertDebtDBException();
            }
        }else{
            int amt = accountDebtEntity.getDebtAmt()-Math.abs(accountInsertDebt.getAmt());
            accountDebtEntity.setDebtAmt(amt);
            int result = accountDebtMapper.updateByPrimaryKeySelective(accountDebtEntity);
            if(result==0){
                throw new AccountInsertDebtDBException();
            }
        }

    }

    /**
     *  抵扣历史欠款，更新欠款总额，返回抵扣后的金额
     * @param accountInsertDebt
     */
    public int deductionHostoryDebt(AccountInsertDebtReqVO accountInsertDebt) {
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountInsertDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            return accountInsertDebt.getAmt();
        }else{
            int amt = accountDebtEntity.getDebtAmt() + Math.abs(accountInsertDebt.getAmt());
            int currentDebtAmt = accountDebtEntity.getDebtAmt();
            int repaidDebtAmt = 0;
            if(amt >= 0){
                //历史欠款全部抵扣完
                currentDebtAmt = 0;
                repaidDebtAmt = accountDebtEntity.getDebtAmt();
            }else{
                currentDebtAmt = amt;
                repaidDebtAmt = amt;
            }
            accountDebtEntity.setDebtAmt(currentDebtAmt);
            int result = accountDebtMapper.updateByPrimaryKeySelective(accountDebtEntity);
            if(result==0){
                throw new AccountInsertDebtDBException();
            }
            //插入流水记录
            AccountDebtDetailEntity accountDebtDetailEntity = new AccountDebtDetailEntity();

            accountDebtDetailEntity.setMemNo(accountInsertDebt.getMemNo());

            accountDebtDetailEntity.setOrderNo(accountInsertDebt.getOrderNo());

            accountDebtDetailEntity.setRenterOrderNo(accountInsertDebt.getRenterOrderNo());

            accountDebtDetailEntity.setOwnerOrderNo(accountInsertDebt.getOwnerOrderNo());
            /**
            * 类型（订单取消罚金/订单结算欠款）
            */
            accountDebtDetailEntity.setType(DebtTypeEnum.CANCEL.getCode());
            /**
            * 当前欠款
            */
            accountDebtDetailEntity.setCurrentDebtAmt(currentDebtAmt);
            /**
            * 订单欠款
            */
            accountDebtDetailEntity.setOrderDebtAmt(0);
            /**
            * 已还欠款
            */
            accountDebtDetailEntity.setRepaidDebtAmt(repaidDebtAmt);
            accountDebtDetailEntity.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountDebtDetailEntity.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            accountInsertDebt.setType(DebtTypeEnum.CANCEL.getCode());


            accountDebtDetailNoTService.insert(accountDebtDetailEntity);
            return amt > 0 ? amt : 0;
        }



    }
}
