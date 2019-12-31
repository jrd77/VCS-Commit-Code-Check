package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 车主端交车车信息表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface OwnerHandoverCarInfoMapper{

    OwnerHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    int insert(OwnerHandoverCarInfoEntity record);
    
    int insertSelective(OwnerHandoverCarInfoEntity record);

    int updateByPrimaryKey(OwnerHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarInfoEntity record);

    /**
     * 根據消息ID查詢是否存在
     * @param msgId
     * @return
     */
    String queryObjectByMsgId(@Param("msgId") String msgId);

}
