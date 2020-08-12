package com.atzuche.order.accountownerincome.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeWithdrawSplitDetailEntity;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeWithdrawSplitDetailMapper;
import com.atzuche.order.commons.constant.OrderConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益提现金额拆分明细
 *
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:53
 */
@Service
public class AccountOwnerIncomeWithdrawSplitDetailService {

    private static Logger logger = LoggerFactory.getLogger(AccountOwnerIncomeWithdrawSplitDetailService.class);

    @Resource
    private AccountOwnerIncomeWithdrawSplitDetailMapper accountOwnerIncomeWithdrawSplitDetailMapper;


    /**
     * 提现金额拆分记录明细
     *
     * @param record 拆分明细数据
     * @return int 成功记录数
     */
    public int addSecondaryIncomeWithdrawSplitDetail(AccountOwnerIncomeWithdrawSplitDetailEntity record) {
        logger.info("Add secondary income wthdraw amt split detail >> data record is, record:[{}]",
                JSON.toJSONString(record));
        if (Objects.isNull(record)) {
            logger.info("Add secondary income wthdraw amt split detail >> data record is empty.");
            return OrderConstant.ZERO;
        }
        return accountOwnerIncomeWithdrawSplitDetailMapper.insertSelective(record);
    }


    /**
     * 获取指定会员的提现金额拆分记录明细列表
     *
     * @param memNo 会员号
     * @return List<AccountOwnerIncomeWithdrawSplitDetailEntity>
     */
    public List<AccountOwnerIncomeWithdrawSplitDetailEntity> selectByMemNo(Integer memNo) {
        return accountOwnerIncomeWithdrawSplitDetailMapper.selectByMemNo(memNo);
    }


}
