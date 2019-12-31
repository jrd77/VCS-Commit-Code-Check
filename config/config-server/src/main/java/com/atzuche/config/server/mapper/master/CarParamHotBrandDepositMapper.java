package com.atzuche.config.server.mapper.master;

import com.atzuche.config.common.entity.CarParamHotBrandDepositEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 3:28 下午
 **/
@Mapper
public interface CarParamHotBrandDepositMapper {
    List<CarParamHotBrandDepositEntity> findAll();
}
