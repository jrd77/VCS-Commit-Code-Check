package com.atzuche.config.server.mapper.master;

import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.ServicePointEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:05 上午
 **/
@Mapper
public interface ServicePointMapper {

    List<ServicePointEntity> findAll();
}
