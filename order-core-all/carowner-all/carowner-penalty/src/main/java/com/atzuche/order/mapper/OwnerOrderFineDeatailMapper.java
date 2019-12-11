package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerOrderFineDeatailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主订单罚金明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:16:00
 */
@Mapper
public interface OwnerOrderFineDeatailMapper{

    OwnerOrderFineDeatailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderFineDeatailEntity> selectALL();

    int insert(OwnerOrderFineDeatailEntity record);
    
    int insertSelective(OwnerOrderFineDeatailEntity record);

    int updateByPrimaryKey(OwnerOrderFineDeatailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderFineDeatailEntity record);

}
