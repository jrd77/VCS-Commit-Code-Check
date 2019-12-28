package com.atzuche.delivery.mapper;

import com.atzuche.delivery.entity.OwnerHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端交车车信息表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface OwnerHandoverCarInfoMapper{

    OwnerHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    int insert(OwnerHandoverCarInfoEntity record);
    
    int insertSelective(OwnerHandoverCarInfoEntity record);

    int updateByPrimaryKey(OwnerHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarInfoEntity record);

}
