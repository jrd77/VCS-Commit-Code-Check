package com.atzuche.delivery.mapper;

import com.atzuche.delivery.entity.OwnerHandoverCarRemarkEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端交车备注表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface OwnerHandoverCarRemarkMapper{

    OwnerHandoverCarRemarkEntity selectByPrimaryKey(Integer id);

    int insert(OwnerHandoverCarRemarkEntity record);
    
    int insertSelective(OwnerHandoverCarRemarkEntity record);

    int updateByPrimaryKey(OwnerHandoverCarRemarkEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarRemarkEntity record);

}
