package com.atzuche.order.settle.service;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountOldDebtReqVO;
import com.atzuche.order.settle.vo.res.AccountDebtResVO;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.autoyol.commons.web.ErrorCode;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
@Slf4j
public class AccountDebtService{
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;
    @Autowired
    private RemoteOldSysDebtService remoteOldSysDebtService;


    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     */
    public AccountDebtResVO getAccountDebtByMemNo(String memNo) {
        return accountDebtNoTService.getAccountDebtByMemNo(memNo);
    }

    /**
     * 查看账户欠款总和
     * @param memNo
     * @return
     */
    public int getAccountDebtNumByMemNo(String memNo){
        AccountDebtResVO res = getAccountDebtByMemNo(memNo);
        if(Objects.isNull(res) || Objects.isNull(res.getDebtAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return res.getDebtAmt();
    }

    /**
     * 抵扣历史欠款
     *  正数
     * @return
     */
    public int deductDebt(AccountDeductDebtReqVO accountDeductDebt) {
        // 1 参数校验
        Assert.notNull(accountDeductDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountDeductDebt.check();
        // 2 查询用户所有待还的记录
        List<AccountDebtDetailEntity> accountDebtDetailAlls =  accountDebtDetailNoTService.getDebtListByMemNo(accountDeductDebt.getMemNo());
        //3 根据租客还款总额  从用户所有待还款记录中 过滤本次 待还款的记录
        List<AccountDebtDetailEntity> accountDebtDetails = accountDebtDetailNoTService.getDebtListByDebtAll(accountDebtDetailAlls,accountDeductDebt);
        //5更新欠款表 当前欠款数
        accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDebtDetails);
        //6 记录欠款收款详情
        accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebt.getAccountDebtReceivableaDetails());
        //7 更新总欠款表
        accountDebtNoTService.deductAccountDebt(accountDeductDebt);
        return accountDeductDebt.getRealAmt();
    }

    /**
     * 记录用户历史欠款
     */
    public int insertDebt(AccountInsertDebtReqVO accountInsertDebt){
        //1校验
        Assert.notNull(accountInsertDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountInsertDebt.check();
        //2 查询账户欠款
        accountDebtNoTService.productAccountDebt(accountInsertDebt);
        //3 新增欠款明细
        return accountDebtDetailNoTService.insertDebtDetail(accountInsertDebt);

    }
    
    
    /**
     * 抵扣老系统历史欠款
     * @param oldDebtList
     * @return List<AccountOldDebtResVO>
     */
    public List<AccountOldDebtResVO> deductOldDebt(List<AccountOldDebtReqVO> oldDebtList) {
    	// 计算抵扣
    	List<AccountOldDebtResVO> debtResList = calDeductOldDebt(oldDebtList);
    	// 转换个人历史欠款收款记录
    	List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetailEntityList = convertToDebtReceivableaDetail(debtResList);
    	if (accountDebtReceivableaDetailEntityList != null && !accountDebtReceivableaDetailEntityList.isEmpty()) {
    		// 保存欠款收款详情
        	accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDebtReceivableaDetailEntityList);
    	}
    	return debtResList;
    }
    

    /**
     * 抵扣老系统历史欠款
     * @param oldDebtList
     * @return List<AccountOldDebtResVO>
     */
    public List<AccountOldDebtResVO> calDeductOldDebt(List<AccountOldDebtReqVO> oldDebtList) {
    	log.info("抵扣老系统历史欠款AccountDebtService.deductOldDebt oldDebtList=[{}]", oldDebtList);
    	if (oldDebtList == null || oldDebtList.isEmpty()) {
    		return null;
    	}
    	// TODO 查询老系统欠款
    	String memNo = oldDebtList.get(0).getMemNo();
    	Integer oldDebtAmt = remoteOldSysDebtService.getMemBalance(memNo);
    	log.info("老系统欠款 oldDebtAmt=[{}]", oldDebtAmt);
    	if (oldDebtAmt == null || oldDebtAmt == 0) {
    		// 老系统无欠款，无需抵扣
    		log.info("老系统无欠款，无需抵扣");
    		return null;
    	}
    	//oldDebtAmt = Math.abs(oldDebtAmt);
    	// 返回对象列表
    	List<AccountOldDebtResVO> debtResList = new ArrayList<AccountOldDebtResVO>();
    	for (AccountOldDebtReqVO old:oldDebtList) {
    		// 剩余可抵扣欠款金额
    		int surplusAmt = old.getSurplusAmt() == null ? 0:old.getSurplusAmt();
    		if (surplusAmt <= 0) {
    			continue;
    		}
    		// 真实抵扣欠款金额
    		Integer realDebtAmt = 0;
    		AccountOldDebtResVO debtRes = new AccountOldDebtResVO();
    		BeanUtils.copyProperties(old, debtRes);
    		if (surplusAmt >= oldDebtAmt) {
    			// 抵扣完所有
    			realDebtAmt = oldDebtAmt;
    			debtRes.setRealDebtAmt(realDebtAmt);
    			debtResList.add(debtRes);
    			break;
    		} else {
    			// 部分抵扣
    			realDebtAmt = surplusAmt;
    			oldDebtAmt = oldDebtAmt - realDebtAmt;
    			debtRes.setRealDebtAmt(realDebtAmt);
    			debtResList.add(debtRes);
    		}
    	}
    	
    	return debtResList;
    }
    
    /**
     * 转换个人历史欠款收款记录
     * @param debtResList
     * @return List<AccountDebtReceivableaDetailEntity>
     */
    public List<AccountDebtReceivableaDetailEntity> convertToDebtReceivableaDetail(List<AccountOldDebtResVO> debtResList) {
    	if (debtResList == null || debtResList.isEmpty()) {
    		return null;
    	}
    	return debtResList.stream().map(debtRes -> getAccountDebtReceivableaDetailEntity(debtRes)).collect(Collectors.toList());
    }
    
    
    /**
     * 对象转换
     * @param debtRes
     * @return AccountDebtReceivableaDetailEntity
     */
    public AccountDebtReceivableaDetailEntity getAccountDebtReceivableaDetailEntity(AccountOldDebtResVO debtRes) {
    	if (debtRes == null) {
    		return null;
    	}
    	AccountDebtReceivableaDetailEntity receivalea = new AccountDebtReceivableaDetailEntity();
    	receivalea.setAmt(debtRes.getRealDebtAmt());
    	receivalea.setMemNo(debtRes.getMemNo());
    	receivalea.setOrderNo(debtRes.getOrderNo());
    	RenterCashCodeEnum cahsCodeEnum = debtRes.getCahsCodeEnum();
    	if (cahsCodeEnum != null) {
    		receivalea.setSourceCode(cahsCodeEnum.getCashNo());
    		receivalea.setSourceDetail(cahsCodeEnum.getTxt());
    	}
    	return receivalea;
    }
    
}
