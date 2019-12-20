package com.atzuche.order.accountrenterwzdepost.mapper;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 违章费用总表及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositCostMapper{

    AccountRenterWzDepositCostEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterWzDepositCostEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterWzDepositCostEntity record);

    AccountRenterWzDepositCostEntity getWZDepositCostAmt(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
}
