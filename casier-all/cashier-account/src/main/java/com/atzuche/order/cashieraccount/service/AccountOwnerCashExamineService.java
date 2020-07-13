package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeWithdrawSplitDetailEntity;
import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeWithdrawSplitDetailService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.exception.WithdrawalAmtException;
import com.atzuche.order.cashieraccount.exception.WithdrawalBalanceNotEnoughException;
import com.atzuche.order.cashieraccount.exception.WithdrawalTimesLimitException;
import com.atzuche.order.cashieraccount.mapper.AccountOwnerCashExamineMapper;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.BankCardDTO;
import com.atzuche.order.commons.entity.dto.CashWithdrawalSimpleMemberDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.wallet.api.MemBalanceVO;
import com.autoyol.platformcost.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 提现记录处理类
 *
 * @author seazhao
 */
@Service
public class AccountOwnerCashExamineService {

    @Autowired
    private AccountOwnerCashExamineMapper accountOwnerCashExamineMapper;
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
    @Autowired
    private RemoteAccountService remoteAccountService;
    @Autowired
    private AccountOwnerCashExamineHandleService accountOwnerCashExamineHandleService;
    @Autowired
    private AccountOwnerIncomeWithdrawSplitDetailService accountOwnerIncomeWithdrawSplitDetailService;


    /** 每个账户每日可申请提现成功次数**/
    private static final int DATE_WITHDRAWAL_MAX = 2;
    private final static int FINALZERO = 0;

    /**
     * 根据会员号获取提现记录
     *
     * @param memNo 会员号
     * @return List<AccountOwnerCashExamine>
     */
    public List<AccountOwnerCashExamine> listAccountOwnerCashExamineByMemNo(Integer memNo) {
        return accountOwnerCashExamineMapper.listAccountOwnerCashExamineByMemNo(memNo);
    }


    /**
     * 会员提现处理
     *
     * @param req       请求参数
     * @param simpleMem 会员信息(包含老交易会员收益信息)
     * @param income    新交易会员收益信息
     * @param bankCard  提现银行卡信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void memberWithdrawalHandle(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem,
                                       AccountOwnerIncomeEntity income, BankCardDTO bankCard) {
        int surplusAmt = Integer.parseInt(req.getAmt());
        // 计算老交易提现金额
        int oldWithdrawableCash = calculateWithdrawableCash(surplusAmt, simpleMem.getBalance());
        surplusAmt = surplusAmt - oldWithdrawableCash;

        int newWithdrawableCash = 0;
        int secondaryWithdrawableCash = 0;
        if (Objects.nonNull(income)) {
            // 计算新交易提现金额(非二清)
            if (Objects.nonNull(income.getIncomeAmt())) {
                newWithdrawableCash = calculateWithdrawableCash(surplusAmt, income.getIncomeAmt());
                surplusAmt = surplusAmt - newWithdrawableCash;
            }
            // 计算新交易提现金额(二清)
            if (Objects.nonNull(income.getSecondaryIncomeAmt())) {
                secondaryWithdrawableCash = calculateWithdrawableCash(surplusAmt, income.getSecondaryIncomeAmt());
                surplusAmt = surplusAmt - secondaryWithdrawableCash;
            }
        }
        if (surplusAmt != OrderConstant.ZERO) {
            throw new WithdrawalBalanceNotEnoughException();
        }
        // 提现记录入库操作
        AccountOwnerCashExamine record = convertAccountOwnerCashExamine(req, bankCard, simpleMem);
        String nowDate = CommonUtils.formatTime(LocalDateTime.now(), CommonUtils.FORMAT_STR_LONG);
        //老交易提现金额入库
        String serialNumber = nowDate + OrderConstant.ONE + req.getMemNo() + oldWithdrawableCash;
        Integer oldId = accountOwnerCashExamineHandleService.oldWithdrawableCashHandle(record, oldWithdrawableCash,
                serialNumber);

        //新交易提现金额入库(非二清)
        serialNumber = nowDate + OrderConstant.TWO + req.getMemNo() + newWithdrawableCash;
        Integer newId = accountOwnerCashExamineHandleService.newWithdrawableCashHandle(record, income,
                newWithdrawableCash, serialNumber);

        //新交易提现金额入库(二清)
        serialNumber = nowDate + OrderConstant.THREE + req.getMemNo() + secondaryWithdrawableCash;
        Integer secondaryId = accountOwnerCashExamineHandleService.secondaryWithdrawableCashHandle(record, income, secondaryWithdrawableCash,
                serialNumber, req.getDynamicCode());

        // 记录提现金额拆分明细
        AccountOwnerIncomeWithdrawSplitDetailEntity splitDetailEntity = new AccountOwnerIncomeWithdrawSplitDetailEntity();
        splitDetailEntity.setMemNo(req.getMemNo());
        splitDetailEntity.setWithdrawAmt(Integer.parseInt(req.getAmt()));
        splitDetailEntity.setOldTransWithdrawAmt(oldWithdrawableCash);
        splitDetailEntity.setNewTransWithdrawAmt(newWithdrawableCash);
        splitDetailEntity.setSecondaryWithdrawAmt(secondaryWithdrawableCash);
        splitDetailEntity.setCashExamineIds(oldId + "," + newId + "," + secondaryId);
        int result = accountOwnerIncomeWithdrawSplitDetailService.addSecondaryIncomeWithdrawSplitDetail(splitDetailEntity);
    }

    /**
     * 计算收益总额中可提现的金额
     *
     * @param surplusAmt 剩余提现金额
     * @param balance    收益总额
     * @return int 可提现金额
     */
    private int calculateWithdrawableCash(int surplusAmt, int balance) {
        if (surplusAmt <= OrderConstant.ZERO || balance <= OrderConstant.ZERO) {
            return OrderConstant.ZERO;
        }
        return surplusAmt >= balance ? surplusAmt - balance : surplusAmt;
    }


    /**
     * 保存提现记录
     *
     * @param req       请求参数
     * @param simpleMem 会员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAccountOwnerCashExamine(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem) {
        if (req == null) {
            return;
        }
        Integer cardId = StringUtils.isBlank(req.getCardId()) ? null : Integer.valueOf(req.getCardId());
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
        if (memBalanceVO != null && memBalanceVO.getBalance() != null && memBalanceVO.getBalance() > 0) {
            simpleMem.setBalance(memBalanceVO.getBalance());
        } else {
            simpleMem.setBalance(0);
        }
        // 检验
        check(req, simpleMem, incomeEntity);
        // 抵扣老balance
        int oldDeductAmt = deductionOldBalance(req, simpleMem, incomeEntity);
        // 抵扣新balance
        int newDeductAmt = deductionNewBalance(req, simpleMem, incomeEntity);

        // 转换成提现记录
        AccountOwnerCashExamine record = convertAccountOwnerCashExamine(req, bankCard, simpleMem);
        String nowDate = CommonUtils.formatTime(LocalDateTime.now(), CommonUtils.FORMAT_STR_LONG);
        if (oldDeductAmt > 0) {
            record.setSerialNumber(nowDate + "1" + req.getMemNo() + oldDeductAmt);
            record.setBalanceFlag(0);
            record.setAmt(oldDeductAmt);
            // 保存提现记录
            accountOwnerCashExamineMapper.insertSelective(record);
        }
        if (newDeductAmt > 0) {
            record.setId(null);
            record.setSerialNumber(nowDate + "2" + req.getMemNo() + newDeductAmt);
            record.setBalanceFlag(1);
            record.setAmt(newDeductAmt);
            // 保存提现记录
            accountOwnerCashExamineMapper.insertSelective(record);
        }
        if (newDeductAmt > 0) {
            // 抵扣新的balance
            int incomeAmt = 0;
            if (incomeEntity != null && incomeEntity.getIncomeAmt() != null) {
                incomeAmt = incomeEntity.getIncomeAmt();
            }
            incomeAmt = incomeAmt - newDeductAmt;
            if (incomeAmt < 0) {
                throw new WithdrawalBalanceNotEnoughException();
            }
            incomeEntity.setIncomeAmt(incomeAmt);
            accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(incomeEntity);
        }
        if (oldDeductAmt > 0) {
            // 调服务抵扣老的balance
            remoteAccountService.deductBalance(req.getMemNo(), oldDeductAmt);
        }
    }

    /**
     * 抵扣老balance
     *
     * @param req 请求参数
     * @param simpleMem 会员信息
     * @param incomeEntity 会员收益信息
     * @return int
     */
    public int deductionOldBalance(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
        // 当前提现金额
        int amt = StringUtils.isBlank(req.getAmt()) ? 0 : Integer.valueOf(req.getAmt());
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
        return deductOldBalance;
    }

    /**
     * 抵扣新balance
     *
     * @param req 请求参数
     * @param simpleMem 会员信息
     * @param incomeEntity 会员收益信息
     * @return int
     */
    public int deductionNewBalance(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
        // 当前提现金额
        int amt = StringUtils.isBlank(req.getAmt()) ? 0 : Integer.valueOf(req.getAmt());
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
        return surplusBalance;
    }


    /**
     * 会员提现校验
     *
     * @param req       请求参数
     * @param simpleMem 会员信息
     */
    public void check(AccountOwnerCashExamineReqVO req, CashWithdrawalSimpleMemberDTO simpleMem, AccountOwnerIncomeEntity incomeEntity) {
        LocalDateTime now = LocalDateTime.now();
        String date = CommonUtils.formatTime(now, CommonUtils.FORMAT_STR_DATE);
        // 查询今天提现记录数
        Integer count = accountOwnerCashExamineMapper.getCountByMemNoAndDateTime(req.getMemNo(), date);
        count = count == null ? 0 : count;
        // 一天最大提现次数
        int limitSize = req.getLimitSize() == null ? DATE_WITHDRAWAL_MAX : req.getLimitSize();
        if (count >= limitSize) {
            throw new WithdrawalTimesLimitException();
        }

        // 当前提现金额
        int amt = StringUtils.isBlank(req.getAmt()) ? 0 : Integer.parseInt(req.getAmt());
        // 最低可提现金额
        int cashMinAmt = req.getCashMinAmt() == null ? 10 : req.getCashMinAmt();
        if (amt < cashMinAmt) {
            throw new WithdrawalAmtException("您的提现金额不能小于最低提现金额");
        }

        // 老系统可提现金额
        int balance = 0;
        if (simpleMem != null && simpleMem.getBalance() != null) {
            balance = simpleMem.getBalance();
        }
        //新系统可提现金额
        int incomeAmt = 0;
        if (Objects.nonNull(incomeEntity)) {
            if (Objects.nonNull(incomeEntity.getIncomeAmt())) {
                incomeAmt = incomeAmt + incomeEntity.getIncomeAmt();
            }

            if (Objects.nonNull(incomeEntity.getSecondaryIncomeAmt())) {
                incomeAmt = incomeAmt + incomeEntity.getSecondaryIncomeAmt();
            }
        }

        if (amt > (balance + incomeAmt)) {
            throw new WithdrawalAmtException("您的提现金额不可大于可提现余额");
        }
    }


    /**
     * 转换成提现记录
     *
     * @param req 请求参数
     * @param bankCard 提现银行卡信息
     * @param simpleMem 会员信息
     * @return AccountOwnerCashExamine 提现记录
     */
    private AccountOwnerCashExamine convertAccountOwnerCashExamine(AccountOwnerCashExamineReqVO req, BankCardDTO bankCard, CashWithdrawalSimpleMemberDTO simpleMem) {
        AccountOwnerCashExamine record = new AccountOwnerCashExamine();
        if (req != null) {
            record.setMemNo(req.getMemNo() == null ? null : Integer.valueOf(req.getMemNo()));
        }
        if (bankCard != null) {
            record.setBankName(bankCard.getBankName() == null ? null : Integer.valueOf(bankCard.getBankName()));
            record.setBranchBankName(bankCard.getBranchBankName());
            record.setCardHolder(bankCard.getCardHolder());
            record.setCardNo(bankCard.getCardNo());
            record.setCity(bankCard.getCity());
            record.setProvince(bankCard.getProvince());
        }
        if (simpleMem != null) {
            record.setMobile(simpleMem.getMobile() == null ? null : Long.valueOf(simpleMem.getMobile()));
            record.setRealName(simpleMem.getRealName());
        }
        record.setStatus(FINALZERO);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        record.setRequestBatchCode(uuid);
        return record;
    }

}
