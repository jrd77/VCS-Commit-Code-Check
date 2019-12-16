package com.atzuche.order.renterwz.mapper;

import com.atzuche.order.renterwz.entity.RenterEventWzStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端违章处理状态表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 */
@Mapper
public interface RenterEventWzStatusMapper{

    RenterEventWzStatusEntity selectByPrimaryKey(Integer id);

    List<RenterEventWzStatusEntity> selectALL();

    int insert(RenterEventWzStatusEntity record);
    
    int insertSelective(RenterEventWzStatusEntity record);

    int updateByPrimaryKey(RenterEventWzStatusEntity record);
    
    int updateByPrimaryKeySelective(RenterEventWzStatusEntity record);

}
