package com.atzuche.order.accountownerincome.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerSecondaryIncomeInoutDetailEntity;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerSecondaryIncomeInoutDetailMapper;
import com.atzuche.order.commons.constant.OrderConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


/**
 * 车主二清收益资金(冻结不可提现部分金额)进出明细
 *
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:52
 */
@Service
public class AccountOwnerSecondaryIncomeInoutDetailService {

    private static Logger logger = LoggerFactory.getLogger(AccountOwnerSecondaryIncomeInoutDetailService.class);

    @Resource
    private AccountOwnerSecondaryIncomeInoutDetailMapper accountOwnerSecondaryIncomeInoutDetailMapper;


    /**
     * 新增进出明细
     *
     * @param record 进出明细数据
     * @return int 成功记录数
     */
    public int addSecondaryIncomeInoutDetail(AccountOwnerSecondaryIncomeInoutDetailEntity record) {
        logger.info("Add secondary income in or out detail >> data record is, record:[{}]", JSON.toJSONString(record));
        if (Objects.isNull(record)) {
            logger.info("Add secondary income in or out detail >> data record is empty.");
            return OrderConstant.ZERO;
        }
        return accountOwnerSecondaryIncomeInoutDetailMapper.insertSelective(record);
    }


    /**
     * 获取指定收益记录的进出明细列表
     *
     * @param ownerIncomeId 车主收益ID
     * @return List<AccountOwnerSecondaryIncomeInoutDetailEntity>
     */
    public List<AccountOwnerSecondaryIncomeInoutDetailEntity> selectByOwnerIncomeId(Integer ownerIncomeId) {
        return accountOwnerSecondaryIncomeInoutDetailMapper.selectByOwnerIncomeId(ownerIncomeId);
    }


}
