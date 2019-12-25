package com.atzuche.order.accountrenterrentcost.mapper;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租车费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Mapper
public interface AccountRenterCostDetailMapper{

    AccountRenterCostDetailEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterCostDetailEntity record);
    
}
