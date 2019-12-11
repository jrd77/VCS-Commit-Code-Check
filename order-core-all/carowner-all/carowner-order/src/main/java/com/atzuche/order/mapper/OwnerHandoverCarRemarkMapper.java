package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端交车备注表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 */
@Mapper
public interface OwnerHandoverCarRemarkMapper{

    OwnerHandoverCarRemarkEntity selectByPrimaryKey(Integer id);

    List<OwnerHandoverCarRemarkEntity> selectALL();

    int insert(OwnerHandoverCarRemarkEntity record);
    
    int insertSelective(OwnerHandoverCarRemarkEntity record);

    int updateByPrimaryKey(OwnerHandoverCarRemarkEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarRemarkEntity record);

}
