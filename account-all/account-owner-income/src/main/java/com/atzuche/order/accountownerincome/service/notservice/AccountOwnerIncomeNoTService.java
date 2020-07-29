package com.atzuche.order.accountownerincome.service.notservice;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerSecondaryIncomeInoutDetailEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeMapper;
import com.atzuche.order.accountownerincome.service.AccountOwnerSecondaryIncomeInoutDetailService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.orderDetailDto.AccountOwnerIncomeListDTO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
@Slf4j
public class AccountOwnerIncomeNoTService {

    @Autowired
    private AccountOwnerIncomeMapper accountOwnerIncomeMapper;

    @Autowired
    private AccountOwnerIncomeDetailMapper accountOwnerIncomeDetailMapper;

    @Autowired
    private AccountOwnerSecondaryIncomeInoutDetailService accountOwnerSecondaryIncomeInoutDetailService;


    public AccountOwnerIncomeEntity getOwnerIncome(String memNo) {
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        if (Objects.isNull(accountOwnerIncome) || Objects.isNull(accountOwnerIncome.getId())) {
            accountOwnerIncome = new AccountOwnerIncomeEntity();
            accountOwnerIncome.setMemNo(memNo);
            accountOwnerIncome.setVersion(NumberUtils.INTEGER_ONE);
            accountOwnerIncome.setIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncome.setSecondaryIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncome.setSecondaryFreezeIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncomeMapper.insertSelective(accountOwnerIncome);
        }
        return accountOwnerIncome;
    }

    public AccountOwnerIncomeEntity getOwnerIncomeByMemNO(String memNo) {
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        if (Objects.isNull(accountOwnerIncome) || Objects.isNull(accountOwnerIncome.getId())) {
            accountOwnerIncome = new AccountOwnerIncomeEntity();
            accountOwnerIncome.setIncomeAmt(OrderConstant.ZERO);
            accountOwnerIncome.setSecondaryIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncome.setSecondaryFreezeIncomeAmt(NumberUtils.INTEGER_ZERO);
        }
        return accountOwnerIncome;
    }

    /**
     * 更新车主收益
     * <p>注:适用于收益/追加收益审核通过累加对应收益</p>
     * <p>注:二清订单收益进入二清冻结(非实时到账)收益字段</p>
     *
     * @param accountOwnerIncomeDetail 车主收益明细
     * @param isSecondFlag             是否二清订单
     */
    public void updateOwnerIncomeAmt(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail, boolean isSecondFlag) {
        log.info("Update owner income info. param is, accountOwnerIncomeDetail:[{}], isSecondFlag:[{}]",
                JSON.toJSONString(accountOwnerIncomeDetail), isSecondFlag);

        AccountOwnerIncomeEntity accountOwnerIncome = getOwnerIncome(accountOwnerIncomeDetail.getMemNo());
        log.info("Original owner income info. accountOwnerIncome:[{}]", JSON.toJSONString(accountOwnerIncome));

        if (isSecondFlag) {
            int orgiSecondaryFreezeIncomeAmt = Objects.isNull(accountOwnerIncome.getSecondaryFreezeIncomeAmt()) ?
                    OrderConstant.ZERO : accountOwnerIncome.getSecondaryFreezeIncomeAmt();
            int secondaryFreezeIncomeAmt = orgiSecondaryFreezeIncomeAmt + accountOwnerIncomeDetail.getAmt();
            if (secondaryFreezeIncomeAmt < OrderConstant.ZERO) {
                throw new AccountOwnerIncomeExamineException();
            }
            accountOwnerIncome.setSecondaryFreezeIncomeAmt(secondaryFreezeIncomeAmt);
            // 添加会员二清收益资金进出明细
            AccountOwnerSecondaryIncomeInoutDetailEntity record = new AccountOwnerSecondaryIncomeInoutDetailEntity();
            record.setMemNo(accountOwnerIncomeDetail.getMemNo());
            record.setOwnerIncomeId(accountOwnerIncome.getId());
            record.setIncomeAmtBefore(orgiSecondaryFreezeIncomeAmt);
            record.setInOutAmt(accountOwnerIncomeDetail.getAmt());
            record.setIncomeAmtAfter(secondaryFreezeIncomeAmt);
            log.info("Add secondary income in or out detail. record:[{}]", JSON.toJSONString(record));
            int result = accountOwnerSecondaryIncomeInoutDetailService.addSecondaryIncomeInoutDetail(record);
            log.info("Add secondary income in or out detail. result:[{}]", result);
        } else {
            int incomeAmt = accountOwnerIncome.getIncomeAmt() + accountOwnerIncomeDetail.getAmt();
            if (incomeAmt < OrderConstant.ZERO) {
                throw new AccountOwnerIncomeExamineException();
            }
            accountOwnerIncome.setIncomeAmt(incomeAmt);
        }
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncome);
        if (result == OrderConstant.ZERO) {
            throw new AccountOwnerIncomeExamineException();
        }
    }


    /**
     * 更新车主收益(收益抵充)
     * <p>注:适用于追加收益为负值且审核通过</p>
     *
     * @param accountOwnerIncomeDetail 车主收益明细
     * @param isSecondFlag             是否二清订单
     */
    public void updateOwnerIncomeCost(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail, boolean isSecondFlag) {
        log.info("Update owner income cost info. param is, accountOwnerIncomeDetail:[{}], isSecondFlag:[{}]",
                JSON.toJSONString(accountOwnerIncomeDetail), isSecondFlag);

        AccountOwnerIncomeEntity accountOwnerIncome = getOwnerIncome(accountOwnerIncomeDetail.getMemNo());
        log.info("Original owner income cost info. accountOwnerIncome:[{}]", JSON.toJSONString(accountOwnerIncome));

        if (isSecondFlag) {
            int orgiSecondaryIncomeAmt = Objects.isNull(accountOwnerIncome.getSecondaryIncomeAmt()) ?
                    OrderConstant.ZERO : accountOwnerIncome.getSecondaryIncomeAmt();
            int secondaryIncomeAmt = orgiSecondaryIncomeAmt + accountOwnerIncomeDetail.getAmt();
            if (secondaryIncomeAmt < OrderConstant.ZERO) {
                throw new AccountOwnerIncomeExamineException();
            }
            accountOwnerIncome.setSecondaryIncomeAmt(secondaryIncomeAmt);
        } else {
            int incomeAmt = accountOwnerIncome.getIncomeAmt() + accountOwnerIncomeDetail.getAmt();
            if (incomeAmt < OrderConstant.ZERO) {
                throw new AccountOwnerIncomeExamineException();
            }
            accountOwnerIncome.setIncomeAmt(incomeAmt);
        }
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncome);
        if (result == OrderConstant.ZERO) {
            throw new AccountOwnerIncomeExamineException();
        }
    }


    /**
     * 更新车主收益(提现)
     *
     * @param accountOwnerIncomeEntity 车主收益信息
     */
    public void updateOwnerIncomeAmtForCashWith(AccountOwnerIncomeEntity accountOwnerIncomeEntity) {
        if (accountOwnerIncomeEntity == null) {
            throw new AccountOwnerIncomeExamineException();
        }
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncomeEntity);
        if (result == 0) {
            throw new AccountOwnerIncomeExamineException();
        }
    }

    public AccountOwnerIncomeEntity getOwnerIncomeByMemNo(String memNo) {

        return accountOwnerIncomeMapper.selectByMemNo(memNo);
    }

    /**
     * 获取会员总收益金额
     *
     * @param memNo 会员号
     * @return int
     */
    public int getOwnerTotalIncomeByMemNo(String memNo) {
        AccountOwnerIncomeEntity incomeEntity = accountOwnerIncomeMapper.selectByMemNo(memNo);
        return calOwnerTotalIncomeAmt(incomeEntity);
    }

    /**
     * 更新收益并保存明细
     *
     * @param accountOwnerIncomeDetail 车主收益明细
     * @param isSecondFlag             是否二清订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTotalIncomeAndSaveDetail(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail, boolean isSecondFlag) {
        // 保存收益详情
        accountOwnerIncomeDetailMapper.insertSelective(accountOwnerIncomeDetail);
        // 更新收益
        updateOwnerIncomeAmt(accountOwnerIncomeDetail, isSecondFlag);
    }

    public List<AccountOwnerIncomeListDTO> getIncomTotalListByMemNoList(List<Integer> memNoList) {
        List<AccountOwnerIncomeEntity> accountOwnerIncomeEntities = accountOwnerIncomeMapper.selectByMemNoList(memNoList);
        List<AccountOwnerIncomeListDTO> accountOwnerIncomeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountOwnerIncomeEntities)) {
            for (AccountOwnerIncomeEntity accountOwnerIncomeEntity : accountOwnerIncomeEntities) {
                AccountOwnerIncomeListDTO accountOwnerIncomeListDTO = new AccountOwnerIncomeListDTO();
                accountOwnerIncomeListDTO.setIncomeAmt(calOwnerTotalIncomeAmt(accountOwnerIncomeEntity));
                accountOwnerIncomeListDTO.setMemNo(accountOwnerIncomeEntity.getMemNo());
                accountOwnerIncomeList.add(accountOwnerIncomeListDTO);
            }
        }
        return accountOwnerIncomeList;
    }


    /**
     * 车主收益信息变更以及收益明细添加
     *
     * @param accountOwnerIncomeDetail 车主收益明细
     * @param isSecondFlag             是否二清订单
     */
    public void updateOwnerIncomeInfo(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail, boolean isSecondFlag) {
        log.info("AccountOwnerIncomeNoTService.updateOwnerIncomeInfo >> accountOwnerIncomeDetail:[{}], " +
                "isSecondFlag:[{}]", JSON.toJSONString(accountOwnerIncomeDetail), isSecondFlag);
        // 新增车主收益明细
        accountOwnerIncomeDetailMapper.insertSelective(accountOwnerIncomeDetail);
        // 更新车主收益金额
        updateOwnerIncomeCost(accountOwnerIncomeDetail, isSecondFlag);
    }


    public int addAccountOwnerIncomeDetail(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail) {
        if(Objects.isNull(accountOwnerIncomeDetail)) {
            return OrderConstant.ZERO;
        }
        // 新增车主收益明细
        return accountOwnerIncomeDetailMapper.insertSelective(accountOwnerIncomeDetail);
    }

    /**
     * 计算车主总收益金额
     *
     * @param incomeEntity 车主收益信息
     * @return 总收益金额
     */
    private int calOwnerTotalIncomeAmt(AccountOwnerIncomeEntity incomeEntity) {
        if (Objects.isNull(incomeEntity)) {
            return OrderConstant.ZERO;
        }
        int totalIncomeAmt = 0;
        if (Objects.nonNull(incomeEntity.getIncomeAmt())) {
            totalIncomeAmt = totalIncomeAmt + incomeEntity.getIncomeAmt();
        }
        if (Objects.nonNull(incomeEntity.getSecondaryIncomeAmt())) {
            totalIncomeAmt = totalIncomeAmt + incomeEntity.getSecondaryIncomeAmt();
        }

        if (Objects.nonNull(incomeEntity.getSecondaryFreezeIncomeAmt())) {
            totalIncomeAmt = totalIncomeAmt + incomeEntity.getSecondaryFreezeIncomeAmt();
        }
        return totalIncomeAmt;
    }
}
