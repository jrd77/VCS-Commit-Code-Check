package com.atzuche.order.renterhandover.mapper;

import com.atzuche.order.renterhandover.entity.RenterHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客端交车备注表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:37:37
 */
@Mapper
public interface RenterHandoverCarRemarkMapper{

    RenterHandoverCarRemarkEntity selectByPrimaryKey(Integer id);

    List<RenterHandoverCarRemarkEntity> selectALL();

    int insert(RenterHandoverCarRemarkEntity record);
    
    int insertSelective(RenterHandoverCarRemarkEntity record);

    int updateByPrimaryKey(RenterHandoverCarRemarkEntity record);
    
    int updateByPrimaryKeySelective(RenterHandoverCarRemarkEntity record);

}
