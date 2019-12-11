package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterStopCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 停运费结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Mapper
public interface AccountRenterStopCostSettleDetailMapper{

    AccountRenterStopCostSettleDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterStopCostSettleDetailEntity> selectALL();

    int insert(AccountRenterStopCostSettleDetailEntity record);
    
    int insertSelective(AccountRenterStopCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountRenterStopCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterStopCostSettleDetailEntity record);

}
