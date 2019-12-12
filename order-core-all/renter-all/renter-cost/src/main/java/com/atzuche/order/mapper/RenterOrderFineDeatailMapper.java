package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Mapper
public interface RenterOrderFineDeatailMapper{

    RenterOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    List<RenterOrderFineDeatailEntity> selectALL();

    int insert(RenterOrderFineDeatailEntity record);
    
    int insertSelective(RenterOrderFineDeatailEntity record);

    int updateByPrimaryKey(RenterOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderFineDeatailEntity record);

}
