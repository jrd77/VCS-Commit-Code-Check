package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountOwnerCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Mapper
public interface AccountOwnerCostSettleMapper{

    AccountOwnerCostSettleEntity selectByPrimaryKey(Integer id);

    List<AccountOwnerCostSettleEntity> selectALL();

    int insert(AccountOwnerCostSettleEntity record);
    
    int insertSelective(AccountOwnerCostSettleEntity record);

    int updateByPrimaryKey(AccountOwnerCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerCostSettleEntity record);

}
