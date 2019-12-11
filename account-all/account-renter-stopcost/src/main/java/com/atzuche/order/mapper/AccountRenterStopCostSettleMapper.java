package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterStopCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 停运费状态及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Mapper
public interface AccountRenterStopCostSettleMapper{

    AccountRenterStopCostSettleEntity selectByPrimaryKey(Integer id);

    List<AccountRenterStopCostSettleEntity> selectALL();

    int insert(AccountRenterStopCostSettleEntity record);
    
    int insertSelective(AccountRenterStopCostSettleEntity record);

    int updateByPrimaryKey(AccountRenterStopCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterStopCostSettleEntity record);

}
