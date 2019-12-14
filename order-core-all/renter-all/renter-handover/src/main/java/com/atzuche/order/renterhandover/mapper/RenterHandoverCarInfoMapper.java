package com.atzuche.order.renterhandover.mapper;

import com.atzuche.order.renterhandover.entity.RenterHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客交车车信息表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:37:37
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
