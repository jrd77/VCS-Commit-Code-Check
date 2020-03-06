package com.atzuche.order.cashieraccount.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.exception.WithdrawalAmtException;
import com.atzuche.order.cashieraccount.exception.WithdrawalBalanceNotEnoughException;
import com.atzuche.order.cashieraccount.exception.WithdrawalTimesLimitException;
import com.atzuche.order.cashieraccount.mapper.AccountOwnerCashExamineMapper;
import com.atzuche.order.commons.entity.dto.BankCardDTO;
import com.atzuche.order.commons.entity.dto.CashWithdrawalSimpleMemberDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.wallet.api.MemBalanceVO;
import com.autoyol.platformcost.CommonUtils;

@Service
public class AccountOwnerCashExamineService {
	
	@Autowired
	private AccountOwnerCashExamineMapper accountOwnerCashExamineMapper;
	@Autowired
	private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
	@Autowired
	private RemoteAccountService remoteAccountService;
	// 每个账户每日可申请提现成功次数
	private static final int DATE_WITHDRAWAL_MAX = 2;
	private final static int FINALZERO = 0;

	/**
	 * 根据会员号获取提现记录
	 * @param memNo
	 * @return List<AccountOwnerCashExamine>
	 */
	public List<AccountOwnerCashExamine> listAccountOwnerCashExamineByMemNo(Integer memNo) {
		return accountOwnerCashExamineMapper.listAccountOwnerCashExamineByMemNo(memNo);
	}
	
	/**
	 * 保存提现记录
	 * @param req
	 * @return Integer
	 */
	public void saveAccountOwnerCashExamine(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem) {
		if (req == null) {
			return;
		}
		Integer cardId = StringUtils.isBlank(req.getCardId()) ? null:Integer.valueOf(req.getCardId());
		// 根据id获取银行卡信息
		BankCardDTO bankCard = remoteAccountService.findAccountById(cardId);
		// 获取新订单系统的会员总收益
		AccountOwnerIncomeEntity incomeEntity = accountOwnerIncomeNoTService.getOwnerIncome(req.getMemNo());
		// 调远程获取老系统可提现余额
		MemBalanceVO memBalanceVO = remoteAccountService.getMemBalance(req.getMemNo());
		if (simpleMem == null) {
			simpleMem = new CashWithdrawalSimpleMemberDTO();
		}
		simpleMem.setMemNo(req.getMemNo());
		if (memBalanceVO != null && memBalanceVO.getBalance() != null) {
			simpleMem.setBalance(memBalanceVO.getBalance());
		}
		// 检验
		check(req, bankCard, simpleMem, incomeEntity);
		// 抵扣老balance
		int oldDeductAmt = deductionOldBalance(req, simpleMem, incomeEntity);
		// 抵扣新balance
		int newDeductAmt = deductionNewBalance(req, simpleMem, incomeEntity);
		// 转换成提现记录
		AccountOwnerCashExamine record = convertAccountOwnerCashExamine(req, bankCard, simpleMem);
		String nowDate = CommonUtils.formatTime(LocalDateTime.now(), CommonUtils.FORMAT_STR_LONG);
		if (oldDeductAmt > 0) {
			record.setSerialNumber(nowDate+"1"+req.getMemNo()+req.getAmt());
			record.setBalanceFlag(0);
			// 保存提现记录
			accountOwnerCashExamineMapper.insertSelective(record);
		}
		if (newDeductAmt > 0) {
			record.setId(null);
			record.setSerialNumber(nowDate+"2"+req.getMemNo()+req.getAmt());
			record.setBalanceFlag(1);
			// 保存提现记录
			accountOwnerCashExamineMapper.insertSelective(record);
		}
	}
	
	/**
	 * 抵扣老balance
	 * @param req
	 * @param simpleMem
	 * @param incomeEntity
	 * @return int
	 */
	public int deductionOldBalance(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
		// 当前提现金额
		int amt = StringUtils.isBlank(req.getAmt()) ? 0:Integer.valueOf(req.getAmt());
		// 老系统可提现金额
		int balance = 0;
		if (simpleMem != null && simpleMem.getBalance() != null) {
			balance = simpleMem.getBalance();
		}
		// 抵扣老的balance
		int deductOldBalance = 0;
		if (balance > 0) {
			if (balance >= amt) {
				deductOldBalance = amt;
			} else {
				deductOldBalance = balance;
			}
		}
		if (deductOldBalance > 0) {
			// 调服务抵扣老的balance
			remoteAccountService.deductBalance(req.getMemNo(), deductOldBalance);
		}
		return deductOldBalance;
	}
	
	/**
	 * 抵扣新balance
	 * @param req
	 * @param simpleMem
	 * @param incomeEntity
	 * @return int
	 */
	public int deductionNewBalance(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
		// 当前提现金额
		int amt = StringUtils.isBlank(req.getAmt()) ? 0:Integer.valueOf(req.getAmt());
		// 老系统可提现金额
		int balance = 0;
		if (simpleMem != null && simpleMem.getBalance() != null) {
			balance = simpleMem.getBalance();
		}
		// 剩余可抵扣的
		int surplusBalance = amt;
		if (balance > 0 && balance < amt) {
			surplusBalance = amt - balance;
		} else if (balance >= amt) {
			surplusBalance = 0;
		}
		if (surplusBalance > 0) {
			// 抵扣新的balance
			int incomeAmt = 0;
			if (incomeEntity != null && incomeEntity.getIncomeAmt() != null) {
				incomeAmt = incomeEntity.getIncomeAmt();
			}
			incomeAmt = incomeAmt - surplusBalance;
			if (incomeAmt < 0) {
				throw new WithdrawalBalanceNotEnoughException();
			}
			incomeEntity.setIncomeAmt(incomeAmt);
			accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(incomeEntity);
		}
		return surplusBalance;
	}
	
	
	/**
	 * 校验
	 * @param req
	 * @param bankCard
	 * @param simpleMem
	 * @param incomeAmt
	 */
	public void check(AccountOwnerCashExamineReqVO req, BankCardDTO bankCard, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
		LocalDateTime now = LocalDateTime.now();
		String date = CommonUtils.formatTime(now, CommonUtils.FORMAT_STR_DATE);
		// 查询今天提现记录数
		Integer count = accountOwnerCashExamineMapper.getCountByMemNoAndDateTime(req.getMemNo(), date);
		count = count == null ? 0:count;
		// 一天最大提现次数
		int limitSize = req.getLimitSize() == null ? DATE_WITHDRAWAL_MAX:req.getLimitSize();
		if (count >= limitSize) {
			throw new WithdrawalTimesLimitException();
		}
		// 当前提现金额
		int amt = StringUtils.isBlank(req.getAmt()) ? 0:Integer.valueOf(req.getAmt());
		// 最低可提现金额
		int cashMinAmt = req.getCashMinAmt() == null ? 10:req.getCashMinAmt();
		if (amt < cashMinAmt) {
			throw new WithdrawalAmtException("您的提现金额不能小于最低提现金额");
		}
		// 老系统可提现金额
		int balance = 0;
		if (simpleMem != null && simpleMem.getBalance() != null) {
			balance = simpleMem.getBalance();
		}
		int incomeAmt = 0;
		if (incomeEntity != null && incomeEntity.getIncomeAmt() != null) {
			incomeAmt = incomeEntity.getIncomeAmt();
		}
		if (amt > (balance + incomeAmt)) {
			throw new WithdrawalAmtException("您的提现金额不可大于可提现余额");
		}
	}
	
	
	/**
	 * 转换成提现记录
	 * @param req
	 * @param bankCard
	 * @param simpleMem
	 * @return
	 */
	private AccountOwnerCashExamine convertAccountOwnerCashExamine(AccountOwnerCashExamineReqVO req, BankCardDTO bankCard, CashWithdrawalSimpleMemberDTO simpleMem) {
		AccountOwnerCashExamine record = new AccountOwnerCashExamine();
		if (req != null) {
			record.setAmt(req.getAmt() == null ? null:Integer.valueOf(req.getAmt()));
			record.setMemNo(req.getMemNo() == null ? null:Integer.valueOf(req.getMemNo()));
		}
		if (bankCard != null) {
			record.setBankName(bankCard.getBankName() == null ? null:Integer.valueOf(bankCard.getBankName()));
			record.setBranchBankName(bankCard.getBranchBankName());
			record.setCardHolder(bankCard.getCardHolder());
			record.setCardNo(bankCard.getCardNo());
			record.setCity(bankCard.getCity());
			record.setProvince(bankCard.getProvince());
		}
		if (simpleMem != null) {
			record.setMobile(simpleMem.getMobile() == null ? null:Long.valueOf(simpleMem.getMobile()));
			record.setRealName(simpleMem.getRealName());
		}
		record.setStatus(FINALZERO);
		String uuid = UUID.randomUUID().toString().replace("-", "");
		record.setRequestBatchCode(uuid);
		return record;
	}
}
