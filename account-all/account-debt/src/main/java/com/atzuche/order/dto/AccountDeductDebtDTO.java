package com.atzuche.order.dto;

import com.atzuche.order.entity.AccountDebtDetailEntity;
import com.atzuche.order.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.vo.req.AccountDeductDebtReqVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史欠款还款 对象包装
 * @author haibao.yan
 */
@Data
public class AccountDeductDebtDTO {
    /**
     * 本次实际要还欠款总和
     */
    private Integer amtReal;

    /**
     *  会员号
     */
    private Integer memNo;
    /**
     * 个人所以代还  欠款记录
     */
    private List<AccountDebtDetailEntity>  accountDebtDetailAlls;
    /**
     * 本次要还 的欠款的欠款记录
     */
    private List<AccountDebtDetailEntity>  accountDebtDetailTodos;
    /**
     * 本次欠款还款 收款记录
     */
    private List<AccountDebtReceivableaDetailEntity>  accountDebtReceivableaDetails;
    /**
     * 欠款还款传参
     */
    private AccountDeductDebtReqVO accountDeductDebtReqVO;

    private AccountDeductDebtDTO(){}

    public AccountDeductDebtDTO(AccountDeductDebtReqVO accountDeductDebtReqVO,List<AccountDebtDetailEntity>  accountDebtDetailAll){
        this.accountDeductDebtReqVO=accountDeductDebtReqVO;
        this.accountDebtDetailAlls=accountDebtDetailAll;
        //初始化数据
        init();
    }

    private void init() {
        //1 参数校验
        Assert.notNull(accountDeductDebtReqVO, ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(!CollectionUtils.isEmpty(accountDebtDetailAlls),ErrorCode.PARAMETER_ERROR.getText());
        //2 计算本次要抵扣欠款记录 存入accountDebtDetailTodos 中去
        setMemNo(accountDeductDebtReqVO.getMemNo());
        List<AccountDebtDetailEntity>  accountDebtDetailTodos = new ArrayList<>();
        int amt = Math.abs(accountDeductDebtReqVO.getAmt());
        int amtReal = NumberUtils.INTEGER_ZERO;
        LocalDateTime now = LocalDateTime.now();
        for(int i =0;i<accountDebtDetailAlls.size();i++){
            AccountDebtDetailEntity accountDebtDetailAll = accountDebtDetailAlls.get(i);
            accountDebtDetailAll.setUpdateOp(accountDeductDebtReqVO.getUpdateOp());
            accountDebtDetailAll.setUpdateTime(now);

            amt = amt - Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
            if(amt>0){
                accountDebtDetailAll.setCurrentDebtAmt(NumberUtils.INTEGER_ZERO);
                accountDebtDetailAll.setRepaidDebtAmt(Math.abs(accountDebtDetailAll.getOrderDebtAmt()));
                accountDebtDetailTodos.add(accountDebtDetailAll);
                amtReal = amtReal + Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
            }
            if(amt==0){
                accountDebtDetailAll.setCurrentDebtAmt(NumberUtils.INTEGER_ZERO);
                accountDebtDetailAll.setRepaidDebtAmt(Math.abs(accountDebtDetailAll.getOrderDebtAmt()));
                accountDebtDetailTodos.add(accountDebtDetailAll);
                amtReal = amtReal + Math.abs(accountDebtDetailAll.getCurrentDebtAmt());
                break;
            }
            if(amt<0){
                accountDebtDetailAll.setCurrentDebtAmt(-Math.abs(amt));
                accountDebtDetailAll.setRepaidDebtAmt(accountDebtDetailAll.getCurrentDebtAmt()-accountDebtDetailAll.getOrderDebtAmt());
                accountDebtDetailTodos.add(accountDebtDetailAll);
                amtReal = amtReal + (Math.abs(accountDebtDetailAll.getCurrentDebtAmt()) - Math.abs(amt));
                break;
            }

        }
        //待还 欠款 set 到setAccountDebtDetailTodos
        setAccountDebtDetailTodos(accountDebtDetailTodos);
        setAmtReal(amtReal);
        //3 记录收款数据
        List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetails = new ArrayList<>();
        for(int i =0;i<accountDebtDetailTodos.size();i++){
            AccountDebtDetailEntity accountDebtDetailEntity = accountDebtDetailTodos.get(i);
            AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail = new AccountDebtReceivableaDetailEntity();
            BeanUtils.copyProperties(getAccountDeductDebtReqVO(),accountDebtReceivableaDetail);
            accountDebtReceivableaDetail.setCreateTime(now);
            accountDebtReceivableaDetail.setUpdateTime(now);
            accountDebtReceivableaDetail.setTime(now);
            accountDebtReceivableaDetail.setDebtDetailId(accountDebtDetailEntity.getId());
            accountDebtReceivableaDetail.setOrderNo(accountDebtDetailEntity.getOrderNo());
            accountDebtReceivableaDetails.add(accountDebtReceivableaDetail);
        }
        //  set记录收款数据
        setAccountDebtReceivableaDetails(accountDebtReceivableaDetails);
    }


}
