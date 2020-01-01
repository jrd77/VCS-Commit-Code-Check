package com.atzuche.config.server.mapper.second;

import com.atzuche.config.common.entity.HolidaySettingEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 2:33 下午
 **/
@Mapper
public interface HolidaySettingMapper {
    List<HolidaySettingEntity> findAll();
}
