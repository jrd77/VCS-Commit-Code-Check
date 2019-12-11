package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterStopCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 停运费资金进出明细
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Mapper
public interface AccountRenterStopCostDetailMapper{

    AccountRenterStopCostDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterStopCostDetailEntity> selectALL();

    int insert(AccountRenterStopCostDetailEntity record);
    
    int insertSelective(AccountRenterStopCostDetailEntity record);

    int updateByPrimaryKey(AccountRenterStopCostDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterStopCostDetailEntity record);

}
