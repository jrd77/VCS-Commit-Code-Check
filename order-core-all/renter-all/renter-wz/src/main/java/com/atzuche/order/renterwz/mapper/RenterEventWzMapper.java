package com.atzuche.order.renterwz.mapper;

import com.atzuche.order.renterwz.entity.RenterEventWzEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端违章处理事件表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:23:44
 */
@Mapper
public interface RenterEventWzMapper{

    RenterEventWzEntity selectByPrimaryKey(Integer id);

    List<RenterEventWzEntity> selectALL();

    int insert(RenterEventWzEntity record);
    
    int insertSelective(RenterEventWzEntity record);

    int updateByPrimaryKey(RenterEventWzEntity record);
    
    int updateByPrimaryKeySelective(RenterEventWzEntity record);

}
