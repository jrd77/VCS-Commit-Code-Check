package com.atzuche.order.accountrenterclaim.mapper;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 理赔费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Mapper
public interface AccountRenterClaimCostSettleDetailMapper{

    AccountRenterClaimCostSettleDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterClaimCostSettleDetailEntity> selectALL();

    int insertSelective(AccountRenterClaimCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountRenterClaimCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterClaimCostSettleDetailEntity record);

}
