package com.atzuche.order.accountrenterwzdepost.mapper;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 违章押金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositDetailMapper{

    AccountRenterWzDepositDetailEntity selectByPrimaryKey(Integer id);

    /**
     * 获取指定订单号的违章押金进出流水记录
     * @param orderNo
     * @return
     */
    List<AccountRenterWzDepositDetailEntity> findByOrderNo(String orderNo);


    int insert(AccountRenterWzDepositDetailEntity record);
    
    int insertSelective(AccountRenterWzDepositDetailEntity record);


}
