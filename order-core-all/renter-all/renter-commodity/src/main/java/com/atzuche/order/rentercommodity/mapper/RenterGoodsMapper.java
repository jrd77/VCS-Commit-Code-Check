package com.atzuche.order.rentercommodity.mapper;
import com.atzuche.order.commons.entity.dto.LianHeMaiTongOrderDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租客商品概览表
 * 
 * @author ZhangBin
 * @date 2019-12-17 11:43:16
 */
@Mapper
public interface RenterGoodsMapper{

    RenterGoodsEntity selectByPrimaryKey(Integer id);

    int insert(RenterGoodsEntity record);
    
    int insertSelective(RenterGoodsEntity record);

    int updateByPrimaryKey(RenterGoodsEntity record);
    
    int updateByPrimaryKeySelective(RenterGoodsEntity record);

    RenterGoodsEntity selectByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);


    List<String> queryOrderNosByOrderNosAndCarNo(@Param("carNo") String carNo,@Param("orderNos") List<String> orderNos);

    String queryCarNumByOrderNo(@Param("orderNo") String orderNo);

    RenterGoodsEntity queryCarInfoByOrderNoAndCarNo(@Param("orderNo")String orderNo,@Param("carNo") String carNo);

    RenterGoodsEntity queryInfoByOrderNo(@Param("orderNo")String orderNo);

    RenterGoodsEntity queryByOrderNoAndPlatNum(@Param("orderNo")String orderNo, @Param("platNum")String platNum);

    List<LianHeMaiTongOrderDTO> getByMemNo(@Param("memNo")String memNo);
}
