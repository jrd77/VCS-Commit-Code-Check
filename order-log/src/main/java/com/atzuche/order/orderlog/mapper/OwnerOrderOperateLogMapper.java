package com.atzuche.order.orderlog.mapper;

import com.atzuche.order.orderlog.entity.OwnerOrderOperateLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车主订单操作记录表
 * 
 * @author ZhangBin
 * @date 2019-12-30 20:31:55
 */
@Mapper
public interface OwnerOrderOperateLogMapper{

    OwnerOrderOperateLogEntity selectByPrimaryKey(Integer id);

    int insert(OwnerOrderOperateLogEntity record);
    
    int insertSelective(OwnerOrderOperateLogEntity record);

    int updateByPrimaryKey(OwnerOrderOperateLogEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderOperateLogEntity record);

}
