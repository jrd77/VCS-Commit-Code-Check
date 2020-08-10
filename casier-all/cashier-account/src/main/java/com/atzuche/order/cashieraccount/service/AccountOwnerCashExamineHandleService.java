package com.atzuche.order.cashieraccount.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.exception.WithdrawalBalanceNotEnoughException;
import com.atzuche.order.cashieraccount.exception.WithdrawalException;
import com.atzuche.order.cashieraccount.exception.WithdrawalTimesLimitException;
import com.atzuche.order.cashieraccount.mapper.AccountOwnerCashExamineMapper;
import com.atzuche.order.cashieraccount.service.remote.AutoSecondOpenRemoteService;
import com.atzuche.order.cashieraccount.vo.req.WithdrawalsReqVO;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 提现记录处理
 *
 * @author pengcheng.fu
 * @date 2020/7/8 11:30
 */

@Service
@Slf4j
public class AccountOwnerCashExamineHandleService {

    @Autowired
    private AccountOwnerCashExamineMapper accountOwnerCashExamineMapper;

    @Autowired
    private RemoteAccountService remoteAccountService;

    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;

    @Autowired
    private AutoSecondOpenRemoteService autoSecondOpenRemoteService;

    @Autowired
    private AccountOwnerCashExamineHandleLogService accountOwnerCashExamineHandleLogService;


    /**
     * 老交易提现金入库
     *
     * @param record              提现信息
     * @param oldWithdrawableCash 提现金额
     * @param serialNumber        流水号
     * @return Integer 提现记录ID
     */
    public Integer oldWithdrawableCashHandle(AccountOwnerCashExamine record, int oldWithdrawableCash,
                                             String serialNumber) {
        if (oldWithdrawableCash > OrderConstant.ZERO) {
            // 添加提现记录
            record.setId(null);
            record.setSerialNumber(serialNumber);
            record.setBalanceFlag(OrderConstant.ZERO);
            record.setAmt(oldWithdrawableCash);
            accountOwnerCashExamineMapper.insertSelective(record);
            // 更新老交易车主收益信息并记录处理结果
            int handleResult = OrderConstant.ONE;
            String failCode = "";
            String failMessage = "";
            try {
                remoteAccountService.deductBalance(String.valueOf(record.getMemNo()), oldWithdrawableCash);
            } catch (RemoteCallException re) {
                handleResult = OrderConstant.TWO;
                failCode = re.getErrorCode();
                failMessage = re.getErrorMsg();
            } catch (Exception e) {
                log.error("AccountOwnerCashExamineHandleService.oldWithdrawableCashHandle err.", e);
                handleResult = OrderConstant.TWO;
            }
            accountOwnerCashExamineHandleLogService.addHandleLog(record.getMemNo(), record.getId(), handleResult,
                    failCode, failMessage);
            return record.getId();
        } else {
            log.info("老交易提现金额为零!");
        }
        return null;
    }

    /**
     * 新交易提现金额入库(非二清)提现金额入库
     *
     * @param record              提现信息
     * @param income              会员收益信息
     * @param newWithdrawableCash 提现金额
     * @param serialNumber        流水号
     * @return Integer 提现记录ID
     */
    public Integer newWithdrawableCashHandle(AccountOwnerCashExamine record,
                                             AccountOwnerIncomeEntity income,
                                             int newWithdrawableCash,
                                             String serialNumber) {
        if (newWithdrawableCash > OrderConstant.ZERO) {
            // 添加提现记录
            record.setId(null);
            record.setSerialNumber(serialNumber);
            record.setBalanceFlag(OrderConstant.ONE);
            record.setAmt(newWithdrawableCash);
            accountOwnerCashExamineMapper.insertSelective(record);
            // 更新新交易车主收益信息并记录处理结果
            int handleResult = OrderConstant.ONE;
            try {
                int incomeAmt = 0;
                if (Objects.nonNull(income) && Objects.nonNull(income.getIncomeAmt())) {
                    incomeAmt = income.getIncomeAmt();
                }
                incomeAmt = incomeAmt - newWithdrawableCash;
                if (incomeAmt < 0) {
                    throw new WithdrawalBalanceNotEnoughException();
                }
                income.setIncomeAmt(incomeAmt);
                log.info("A.new owner income info. income:[{}]", JSON.toJSONString(income));
                accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(income);
            } catch (Exception e) {
                log.error("AccountOwnerCashExamineHandleService.newWithdrawableCashHandle err.", e);
                handleResult = OrderConstant.TWO;
            }
            accountOwnerCashExamineHandleLogService.addHandleLog(record.getMemNo(), record.getId(), handleResult);
            return record.getId();
        } else {
            log.info("新交易提现金额入库(非二清)提现金额为零!");
        }
        return null;
    }

    /**
     * 新交易提现金额入库(二清)提现金额
     *
     * @param record                    提现信息
     * @param income                    会员收益信息
     * @param secondaryWithdrawableCash 提现金额
     * @param serialNumber              流水号
     * @param dynamicCode               短信动态验证码
     * @return Integer 提现记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer secondaryWithdrawableCashHandle(AccountOwnerCashExamine record,
                                                   AccountOwnerIncomeEntity income,
                                                   int secondaryWithdrawableCash,
                                                   String serialNumber, String dynamicCode) {
        if (secondaryWithdrawableCash > OrderConstant.ZERO) {
            // 添加提现记录
            record.setId(null);
            record.setSerialNumber(serialNumber);
            record.setBalanceFlag(OrderConstant.ONE);
            record.setSecondCleanFlag(OrderConstant.ONE);
            record.setAmt(secondaryWithdrawableCash);
            accountOwnerCashExamineMapper.insertSelective(record);
            // 更新新交易车主收益信息(二清)并记录处理结果
            try {
                // 调用远程方法提现
                WithdrawalsReqVO reqVO = new WithdrawalsReqVO();
                reqVO.setMemNo(String.valueOf(record.getMemNo()));
                reqVO.setSerialNumber(record.getSerialNumber());
                reqVO.setBindCardNo(record.getCardNo());
                reqVO.setAmount(String.valueOf(record.getAmt()));
                reqVO.setSmsCode(dynamicCode);
                ResponseData responseData = autoSecondOpenRemoteService.sendWithdrawalRequest(reqVO);
                if (StringUtils.equals(responseData.getResCode(), ErrorCode.SUCCESS.getCode())) {
                    // 受理成功后同步提现记录的状态
                    AccountOwnerCashExamine cashExamine = new AccountOwnerCashExamine();
                    cashExamine.setId(record.getId());
                    cashExamine.setStatus(12);
                    accountOwnerCashExamineMapper.updateByPrimaryKeySelective(cashExamine);

                    // 更新新交易车主收益信息
                    int secondaryIncomeAmt = 0;
                    if (Objects.nonNull(income) && Objects.nonNull(income.getSecondaryIncomeAmt())) {
                        secondaryIncomeAmt = income.getSecondaryIncomeAmt();
                    }
                    secondaryIncomeAmt = secondaryIncomeAmt - secondaryWithdrawableCash;
                    income.setSecondaryIncomeAmt(secondaryIncomeAmt);

                    log.info("B.new owner income info. income:[{}]", JSON.toJSONString(income));
                    accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(income);

                    accountOwnerCashExamineHandleLogService.addHandleLog(record.getMemNo(), record.getId(), OrderConstant.ONE,
                            null, null);
                    return record.getId();
                } else {
                    log.info("AccountOwnerCashExamineHandleService.secondaryWithdrawableCashHandle fail. " +
                            "responseData:[{}]", JSON.toJSONString(responseData));
                    throw new WithdrawalException(responseData.getResCode(), responseData.getResMsg());
                }
            } catch (WithdrawalException we) {
                throw we;
            }catch (Exception e) {
                log.error("AccountOwnerCashExamineHandleService.secondaryWithdrawableCashHandle err.", e);
                throw new WithdrawalException(ErrorCode.SYS_ERROR.getCode(), ErrorCode.SYS_ERROR.getText());
            }
        } else {
            log.info("新交易提现金额入库(二清)提现金额为零!");
        }
        return null;
    }


    /**
     * 依据流水号和会员号获取对应的提现记录
     *
     * @param serialNumber 流水号
     * @param memNo        会员号
     * @return AccountOwnerCashExamine 提现记录
     */
    public AccountOwnerCashExamine selectBySerialNumberAndMemNo(String serialNumber, String memNo) {
        return accountOwnerCashExamineMapper.selectBySerialNumberAndMemNo(serialNumber, memNo);
    }

    /**
     * 更新提现记录
     *
     * @param record 数据
     * @return int 成功记录数
     */
    public int updateAccountOwnerCashExamine(AccountOwnerCashExamine record) {
        log.info("Update account owner cash examine. record:[{}]", JSON.toJSONString(record));
        if (Objects.isNull(record)) {
            log.info("Update account owner cash examine. data is empty!");
            return OrderConstant.ZERO;
        }
        return accountOwnerCashExamineMapper.updateByPrimaryKeySelective(record);
    }
}