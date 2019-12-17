package com.atzuche.order.accountrenterrentcost.mapper;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租车费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Mapper
public interface AccountRenterCostSettleDetailMapper{

    AccountRenterCostSettleDetailEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterCostSettleDetailEntity record);
    
    int insertSelective(AccountRenterCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountRenterCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterCostSettleDetailEntity record);

}
