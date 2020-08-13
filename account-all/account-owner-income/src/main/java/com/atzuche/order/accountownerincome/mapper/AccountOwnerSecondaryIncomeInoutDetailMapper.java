package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerSecondaryIncomeInoutDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车主二清收益资金(冻结不可提现部分金额)进出明细
 *
 * @author pengcheng.fu
 * @date 2020-07-07 13:44:52
 */
@Mapper
public interface AccountOwnerSecondaryIncomeInoutDetailMapper {

    /**
     * 依据主键查询
     *
     * @param id 主键
     * @return AccountOwnerSecondaryIncomeInoutDetailEntity
     */
    AccountOwnerSecondaryIncomeInoutDetailEntity selectByPrimaryKey(Integer id);


    /**
     * 依据车主收益记录ID查询进出明细列表
     *
     * @param ownerIncomeId 车主收益记录ID
     * @return List<AccountOwnerSecondaryIncomeInoutDetailEntity>
     */
    List<AccountOwnerSecondaryIncomeInoutDetailEntity> selectByOwnerIncomeId(Integer ownerIncomeId);

    /**
     * 新增
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insert(AccountOwnerSecondaryIncomeInoutDetailEntity record);

    /**
     * 新增（过滤空值）
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insertSelective(AccountOwnerSecondaryIncomeInoutDetailEntity record);

    /**
     * 修改
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKey(AccountOwnerSecondaryIncomeInoutDetailEntity record);

    /**
     * 修改(过滤空值)
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKeySelective(AccountOwnerSecondaryIncomeInoutDetailEntity record);

}
