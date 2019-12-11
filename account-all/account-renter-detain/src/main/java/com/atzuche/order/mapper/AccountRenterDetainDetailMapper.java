package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterDetainDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 暂扣费用进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Mapper
public interface AccountRenterDetainDetailMapper{

    AccountRenterDetainDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterDetainDetailEntity> selectALL();

    int insert(AccountRenterDetainDetailEntity record);
    
    int insertSelective(AccountRenterDetainDetailEntity record);

    int updateByPrimaryKey(AccountRenterDetainDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDetainDetailEntity record);

}
