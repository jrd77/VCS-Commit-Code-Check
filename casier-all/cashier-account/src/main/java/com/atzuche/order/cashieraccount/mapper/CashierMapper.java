package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Mapper
public interface CashierMapper{

    CashierEntity selectByPrimaryKey(Integer id);

    List<CashierEntity> selectALL();

    int insert(CashierEntity record);
    
    int insertSelective(CashierEntity record);

    int updateByPrimaryKey(CashierEntity record);
    
    int updateByPrimaryKeySelective(CashierEntity record);

}
