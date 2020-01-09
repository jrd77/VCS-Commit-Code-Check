package com.atzuche.order.delivery.service.delivery;

import com.atzuche.config.client.api.OilAverageCostConfigSDK;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.utils.MathUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 */
@Service
@Slf4j
public class DeliveryCarInfoPriceService {

    @Autowired
    OilAverageCostConfigSDK oilAverageCostConfigSDK;

    /**
     * 根据类型获取所在城市的油价
     */
    public Double getOilPriceByCityCodeAndType(Integer cityCode, Integer type) {
        List<OilAverageCostEntity> oilAverageCostEntityList = oilAverageCostConfigSDK.getConfig(DeliveryCarInfoConfigContext.builder().build());
        OilAverageCostEntity oilAverageCostEntity = oilAverageCostEntityList.stream().filter(r -> r.getCityCode() == cityCode.intValue() && r.getEngineType() == type.intValue()).findFirst().get();
        if (Objects.isNull(oilAverageCostEntity)) {
            oilAverageCostEntity = oilAverageCostEntityList.stream().filter(r -> r.getCityCode() == 0 && r.getEngineType() == type.intValue()).findFirst().get();
        }
        if (Objects.isNull(oilAverageCostEntity)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到对应的城市油价");
        }
        int molecule = oilAverageCostEntity.getMolecule();
        int denominator = oilAverageCostEntity.getDenominator();
        return MathUtil.div(molecule, denominator, 2);
    }

    /**
     * 设置预环境
     */
    @Builder
    private static class DeliveryCarInfoConfigContext implements ConfigContext {

        @Override
        public boolean preConfig() {
            return false;
        }
    }
}
