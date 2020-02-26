package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


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

    /**
     * 根据车主子订单查询
     * @param ownerOrderNo
     * @return
     */
    OwnerHandoverCarInfoEntity selectByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo,@Param("type") Integer type);

    /**
     * 根据子订单号查询
     * @return
     */
    List<OwnerHandoverCarInfoEntity> selectObjectByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo);


    /**
     * 根据子订单号
     * @return
     */
    List<OwnerHandoverCarInfoEntity> selectOwnerHandovertByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo);

    /**
     * 根据车主订单查询
     *
     * @param orderNo
     * @return
     */
    OwnerHandoverCarInfoEntity selectObjectByOrderNo(@Param("orderNo") String orderNo, @Param("type") Integer type);

    /**
     * 根据车主订单查询
     * @param orderNo
     * @return
     */
    List<OwnerHandoverCarInfoEntity> selectOwnerByOrderNo(@Param("orderNo") String orderNo);

     OwnerHandoverCarInfoEntity selectByOwnerOrderNoAndType(@Param("ownerOrderNo")String ownerOrderNo,@Param("type")Integer type);

}
