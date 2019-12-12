package com.atzuche.order.config.oilpriceconfig;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 3:56 下午
 **/
@Mapper
public interface OilAverageCostMapper {
    /**
     * 读取所有的油价配置数据
     * @return
     */
    public List<OilAverageCostEntity> findAll();
}
