package com.atzuche.order.mapper;

import com.atzuche.order.entity.RenterHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客交车车信息表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:15:28
 */
@Mapper
public interface RenterHandoverCarInfoMapper{

    RenterHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    List<RenterHandoverCarInfoEntity> selectALL();

    int insert(RenterHandoverCarInfoEntity record);
    
    int insertSelective(RenterHandoverCarInfoEntity record);

    int updateByPrimaryKey(RenterHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(RenterHandoverCarInfoEntity record);

}
