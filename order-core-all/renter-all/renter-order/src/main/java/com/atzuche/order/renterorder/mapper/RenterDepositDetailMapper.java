package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车辆押金详情
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:50:13
 */
@Mapper
public interface RenterDepositDetailMapper{

    RenterDepositDetailEntity selectByPrimaryKey(Integer id);

    int insert(RenterDepositDetailEntity record);
    
    int insertSelective(RenterDepositDetailEntity record);

    int updateByPrimaryKey(RenterDepositDetailEntity record);
    
    int updateByPrimaryKeySelective(RenterDepositDetailEntity record);

}
