package com.atzuche.violation.mapper;

import com.atzuche.order.commons.entity.dto.RenterOrderWzDetailLogDTO;
import com.atzuche.order.commons.entity.wz.RenterOrderWzDetailLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 违章明细日志表
 * 
 * @author ZhangBin
 * @date 2020-06-08 14:23:43
 */
@Mapper
public interface RenterOrderWzDetailLogMapper{

    RenterOrderWzDetailLogEntity selectByPrimaryKey(Long id);

    int insertSelective(RenterOrderWzDetailLogEntity record);

    int updateByPrimaryKeySelective(RenterOrderWzDetailLogEntity record);

    List<RenterOrderWzDetailLogEntity> queryList();
}
