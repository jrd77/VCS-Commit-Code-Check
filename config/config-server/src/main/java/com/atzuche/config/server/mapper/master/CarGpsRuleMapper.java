package com.atzuche.config.server.mapper.master;

import com.atzuche.config.common.entity.CarGpsRuleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 2:27 下午
 **/
@Mapper
public interface CarGpsRuleMapper {
    List<CarGpsRuleEntity> findAll();
}
