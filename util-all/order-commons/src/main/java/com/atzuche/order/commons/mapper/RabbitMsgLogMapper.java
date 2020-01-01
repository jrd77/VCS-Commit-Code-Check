package com.atzuche.order.commons.mapper;

import com.atzuche.order.commons.entity.RabbitMsgLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * rabbitmq接收消息记录
 * 
 * @author ZhangBin
 * @date 2019-12-23 19:27:14
 */
@Mapper
public interface RabbitMsgLogMapper{

    RabbitMsgLogEntity selectByPrimaryKey(Integer id);

    RabbitMsgLogEntity selectByUniqueNo(@Param("uniqueNo") String uniqueNo,@Param("businessType") String businessType);

    int insert(RabbitMsgLogEntity record);

    int updateConsume(@Param("uniqueNo") String uniqueNo);

}
