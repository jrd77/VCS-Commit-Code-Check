package com.atzuche.order.accountrenterwzdepost.mapper;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 违章费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositCostSettleDetailMapper{

    AccountRenterWzDepositCostSettleDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterWzDepositCostSettleDetailEntity> selectALL();

    int insertSelective(AccountRenterWzDepositCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountRenterWzDepositCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterWzDepositCostSettleDetailEntity record);
    
    /**
     * 查询违章的结算明细记录，根据订单号。
     * @return
     */
    List<AccountRenterWzDepositCostSettleDetailEntity> selectByOrderNo(@Param("orderNo")String orderNo);
    

}
