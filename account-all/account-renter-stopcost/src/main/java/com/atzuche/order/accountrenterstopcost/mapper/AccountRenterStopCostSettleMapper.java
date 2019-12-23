package com.atzuche.order.accountrenterstopcost.mapper;

import com.atzuche.order.accountrenterstopcost.entity.AccountRenterStopCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    int insert(AccountRenterStopCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterStopCostSettleEntity record);

    AccountRenterStopCostSettleEntity getRenterStopCostAmt(@Param("memNo") String memNo);
}
