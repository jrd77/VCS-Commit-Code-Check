package com.atzuche.config.server.mapper.master;

import com.atzuche.config.common.entity.CarChargeLevelConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CarChargeLevelConfigMapper {
    List<CarChargeLevelConfigEntity> listCarLevelConfigByLevel();
}
