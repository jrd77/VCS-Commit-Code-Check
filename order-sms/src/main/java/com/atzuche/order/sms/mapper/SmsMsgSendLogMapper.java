package com.atzuche.order.sms.mapper;

import com.atzuche.order.sms.entity.SmsMsgSendLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author ZhangBin
 * @date 2020-04-02 20:15:10
 */
@Mapper
public interface SmsMsgSendLogMapper {

    SmsMsgSendLogEntity selectByPrimaryKey(Integer id);

    int insert(SmsMsgSendLogEntity record);
    
    int insertSelective(SmsMsgSendLogEntity record);

    int updateByPrimaryKey(SmsMsgSendLogEntity record);
    
    int updateByPrimaryKeySelective(SmsMsgSendLogEntity record);

    SmsMsgSendLogEntity selectByOrderNoAndMemNo(@Param("orderNo") String orderNo, @Param("memNo") String memNo, @Param("msgType") Integer msgType);

}
