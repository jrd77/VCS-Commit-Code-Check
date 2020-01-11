package com.atzuche.order.accountrenterdetain.mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainDetailEntity;
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

    int insertSelective(AccountRenterDetainDetailEntity record);

    int updateByPrimaryKey(AccountRenterDetainDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDetainDetailEntity record);

   List<AccountRenterDetainDetailEntity> selectByOrderNo(@Param("orderNo")String orderNo);


}
