package com.atzuche.delivery.mapper;

import com.atzuche.delivery.entity.RenterHandoverCarInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租客交车车信息表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 */
@Mapper
public interface RenterHandoverCarInfoMapper{

    RenterHandoverCarInfoEntity selectByPrimaryKey(Integer id);

    int insert(RenterHandoverCarInfoEntity record);
    
    int insertSelective(RenterHandoverCarInfoEntity record);

    int updateByPrimaryKey(RenterHandoverCarInfoEntity record);
    
    int updateByPrimaryKeySelective(RenterHandoverCarInfoEntity record);

}
