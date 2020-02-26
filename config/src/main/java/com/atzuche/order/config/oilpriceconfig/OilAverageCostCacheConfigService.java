package com.atzuche.order.config.oilpriceconfig;

import com.atzuche.order.config.CacheConfigServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 3:48 下午
 **/
@Service
public class OilAverageCostCacheConfigService extends CacheConfigServiceTemplate<List<OilAverageCostEntity>> {
    private final static Logger logger = LoggerFactory.getLogger(OilAverageCostCacheConfigService.class);
    

    @Autowired
    private OilAverageCostMapper oilAverageCostMapper;

    @Override
    protected List<OilAverageCostEntity> loadConfigData() {

        List<OilAverageCostEntity> list =  oilAverageCostMapper.findAll();

        System.out.println("list={}"+list);
        return list;
    }

    @Override
    protected String key() {
        return "Oil_average_cost_table";
    }

    /**
     * 返回指定engineType和cityCode对应的数据,如果对应城市不存在，返回城市为0的数据。如果城市为0的数据也不存在，返回null。
     * @param engineType 动力类型
     *                   动力类型，1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油
     * @param cityCode 城市编码
     * @return
     */
    public OilAverageCostEntity findOilPriceByEngineTypeAndCity(int engineType,int cityCode){
        List<OilAverageCostEntity> oilAverageCostEntities = getConfig();
        for(OilAverageCostEntity entity : oilAverageCostEntities){
            if(engineType==entity.getEngineType()&&cityCode==entity.getCityCode()){
                return entity;
            }
        }
        OilAverageCostEntity oilAverageCostEntity =null;
        if(cityCode>0) {
            logger.warn("没有找到对应城市和动力类型的数据，请注意:engineType={},cityCode={},返回cityCode=0的值", engineType, cityCode);
            //当没有找到对应城市的数据时，返回城市编码为0的数据。
            oilAverageCostEntity = findOilPriceByEngineTypeAndCity(engineType, 0);
        }

        if(oilAverageCostEntity==null){
            logger.error("没有找到默认城市的动力类型engineType={} 对应的油价数据",engineType);
            throw new RuntimeException("ConfigNotFoundException for oilAverageCostConfig");
        }
        return oilAverageCostEntity;
    }


}
