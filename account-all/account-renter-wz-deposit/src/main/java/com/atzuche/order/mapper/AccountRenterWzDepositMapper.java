package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterWzDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 违章押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositMapper{

    AccountRenterWzDepositEntity selectByPrimaryKey(Integer id);

    List<AccountRenterWzDepositEntity> selectALL();

    int insert(AccountRenterWzDepositEntity record);
    
    int insertSelective(AccountRenterWzDepositEntity record);

    int updateByPrimaryKey(AccountRenterWzDepositEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterWzDepositEntity record);

}
