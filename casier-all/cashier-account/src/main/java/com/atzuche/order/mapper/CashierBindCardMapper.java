package com.atzuche.order.mapper;

import com.atzuche.order.entity.CashierBindCardEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 个人免押绑卡信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Mapper
public interface CashierBindCardMapper{

    CashierBindCardEntity selectByPrimaryKey(Integer id);

    List<CashierBindCardEntity> selectALL();

    int insert(CashierBindCardEntity record);
    
    int insertSelective(CashierBindCardEntity record);

    int updateByPrimaryKey(CashierBindCardEntity record);
    
    int updateByPrimaryKeySelective(CashierBindCardEntity record);

}
