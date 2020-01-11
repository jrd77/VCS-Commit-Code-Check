package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterAdditionalDriverEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客端附加驾驶人信息表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:51:58
 */
@Mapper
public interface RenterAdditionalDriverMapper{

    RenterAdditionalDriverEntity selectByPrimaryKey(Integer id);

    int insert(RenterAdditionalDriverEntity record);
    
    int insertSelective(RenterAdditionalDriverEntity record);

    int updateByPrimaryKey(RenterAdditionalDriverEntity record);
    
    int updateByPrimaryKeySelective(RenterAdditionalDriverEntity record);
    
    List<String> listDriverIdByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);

    List<RenterAdditionalDriverEntity> selectByRenterOrderNo(@Param("renterOrderNo")String renterOrderNo);



}
