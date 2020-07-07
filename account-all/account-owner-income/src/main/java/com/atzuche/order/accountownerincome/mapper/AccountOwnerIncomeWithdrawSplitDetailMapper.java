package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeWithdrawSplitDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车主收益提现金额拆分明细
 *
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:53
 */
@Mapper
public interface AccountOwnerIncomeWithdrawSplitDetailMapper {

    /**
     * 依据主键查询
     *
     * @param id 主键
     * @return AccountOwnerIncomeWithdrawSplitDetailEntity
     */
    AccountOwnerIncomeWithdrawSplitDetailEntity selectByPrimaryKey(Integer id);

    /**
     * 依据会员号查询
     *
     * @param memNo 会员号
     * @return List<AccountOwnerIncomeWithdrawSplitDetailEntity>
     */
    List<AccountOwnerIncomeWithdrawSplitDetailEntity> selectByMemNo(Integer memNo);

    /**
     * 新增
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insert(AccountOwnerIncomeWithdrawSplitDetailEntity record);

    /**
     * 新增（过滤空值）
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insertSelective(AccountOwnerIncomeWithdrawSplitDetailEntity record);

    /**
     * 修改
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKey(AccountOwnerIncomeWithdrawSplitDetailEntity record);

    /**
     * 修改(过滤空值)
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKeySelective(AccountOwnerIncomeWithdrawSplitDetailEntity record);

}
