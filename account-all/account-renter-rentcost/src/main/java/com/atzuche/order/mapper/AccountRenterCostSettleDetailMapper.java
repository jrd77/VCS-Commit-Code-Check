package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterCostSettleDetailEntity;
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

    List<AccountRenterCostSettleDetailEntity> selectALL();

    int insert(AccountRenterCostSettleDetailEntity record);
    
    int insertSelective(AccountRenterCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountRenterCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterCostSettleDetailEntity record);

}
