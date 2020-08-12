package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamineHandleLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现记录处理日志
 *
 * @author pengcheng.fu
 * @date 2020-07-17 11:48:27
 */
@Mapper
public interface AccountOwnerCashExamineHandleLogMapper {

    /**
     * 依据主键查询
     *
     * @param id 主键
     * @return AccountOwnerCashExamineHandleLogEntity
     */
    AccountOwnerCashExamineHandleLogEntity selectByPrimaryKey(Integer id);

    /**
     * 新增
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insert(AccountOwnerCashExamineHandleLogEntity record);

    /**
     * 新增（过滤空值）
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int insertSelective(AccountOwnerCashExamineHandleLogEntity record);

    /**
     * 修改
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKey(AccountOwnerCashExamineHandleLogEntity record);

    /**
     * 修改(过滤空值)
     *
     * @param record 数据
     * @return int 成功记录数
     */
    int updateByPrimaryKeySelective(AccountOwnerCashExamineHandleLogEntity record);

}
