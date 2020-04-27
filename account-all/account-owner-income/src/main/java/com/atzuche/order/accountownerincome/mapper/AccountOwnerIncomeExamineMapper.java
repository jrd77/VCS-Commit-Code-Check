package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主收益待审核表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeExamineMapper {

    AccountOwnerIncomeExamineEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountOwnerIncomeExamineEntity record);

    int updateByPrimaryKeySelective(AccountOwnerIncomeExamineEntity record);

    /*根据订单号查询*/
    List<AccountOwnerIncomeExamineEntity> selectByOrderNo(@Param("orderNo") String orderNo, @Param("memNo") String memNo);

    int getTotalAccountOwnerIncomeExamineByOrderNo(String orderNo);

    List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrderAndType(@Param("orderNo") String orderNo, @Param("memNo") String memNo, @Param("type") int type);

    List<AccountOwnerIncomeExamineEntity> selectByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo);

    List<AccountOwnerIncomeExamineEntity> selectByExamineId(@Param("examineId") Integer examineId);

    List<AccountOwnerIncomeExamineEntity> getIncomByOwnerMemAndStatus(@Param("ownerMemeNo") String ownerMemeNo, @Param("statusList") List<Integer> status);

    List<AccountOwnerIncomeExamineEntity> getAccountOwnerIncomeExamineByOrderNo(@Param("orderNo") String orderNo);
}
