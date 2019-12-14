package com.atzuche.order.accountrenterdetain.mapper;

import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainCostEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 暂扣费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Mapper
public interface AccountRenterDetainCostMapper{

    AccountRenterDetainCostEntity selectByPrimaryKey(Integer id);

    List<AccountRenterDetainCostEntity> selectALL();

    int insert(AccountRenterDetainCostEntity record);
    
    int insertSelective(AccountRenterDetainCostEntity record);

    int updateByPrimaryKey(AccountRenterDetainCostEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDetainCostEntity record);

}
