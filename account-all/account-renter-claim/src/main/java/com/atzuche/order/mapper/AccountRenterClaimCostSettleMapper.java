package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterClaimCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 理赔费用/及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Mapper
public interface AccountRenterClaimCostSettleMapper{

    AccountRenterClaimCostSettleEntity selectByPrimaryKey(Integer id);

    List<AccountRenterClaimCostSettleEntity> selectALL();

    int insert(AccountRenterClaimCostSettleEntity record);
    
    int insertSelective(AccountRenterClaimCostSettleEntity record);

    int updateByPrimaryKey(AccountRenterClaimCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterClaimCostSettleEntity record);

}
