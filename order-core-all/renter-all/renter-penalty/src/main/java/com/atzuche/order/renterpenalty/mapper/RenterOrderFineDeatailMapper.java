package com.atzuche.order.renterpenalty.mapper;

import com.atzuche.rentercost.entity.RenterOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
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
