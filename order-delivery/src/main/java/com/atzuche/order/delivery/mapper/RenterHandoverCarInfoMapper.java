package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 租客交车车信息表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface RenterHandoverCarInfoMapper{

    RenterHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    int insert(RenterHandoverCarInfoEntity record);
    
    int insertSelective(RenterHandoverCarInfoEntity record);

    int updateByPrimaryKey(RenterHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(RenterHandoverCarInfoEntity record);

    /**
     * 根据租客子订单查询
     * @param renterOrderNo
     * @return
     */
    List<RenterHandoverCarInfoEntity> selectByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

    /**
     * 根據消息ID查詢是否存在
     * @param msgId
     * @return
     */
    String queryObjectByMsgId(@Param("msgId") String msgId);

}
