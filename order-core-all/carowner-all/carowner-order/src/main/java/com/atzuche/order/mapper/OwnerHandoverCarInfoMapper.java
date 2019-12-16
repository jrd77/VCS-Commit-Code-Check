package com.atzuche.order.mapper;

import com.atzuche.order.entity.OwnerHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端交车车信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 */
@Mapper
public interface OwnerHandoverCarInfoMapper{

    OwnerHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    List<OwnerHandoverCarInfoEntity> selectALL();

    int insert(OwnerHandoverCarInfoEntity record);
    
    int insertSelective(OwnerHandoverCarInfoEntity record);

    int updateByPrimaryKey(OwnerHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(OwnerHandoverCarInfoEntity record);

}
