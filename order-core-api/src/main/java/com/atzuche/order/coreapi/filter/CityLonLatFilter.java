package com.atzuche.order.coreapi.filter;

import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.ServicePointConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.ServicePointEntity;
import com.atzuche.order.commons.GeoUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.exceptions.CityLonLatException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/14 17:54
 * @ Description   :  城市服务点判断
 *  
 */

@Slf4j
@Service
public class CityLonLatFilter implements OrderFilter{
    @Autowired
    private CityConfigSDK cityConfigSDK;
    @Autowired
    private ServicePointConfigSDK servicePointConfigSDK;

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVo = context.getOrderReqVO();
        String carNo = orderReqVo.getCarNo();
        Integer city = orderReqVo.getCityCode() == null ? 0 : Integer.valueOf(orderReqVo.getCityCode());
        Boolean allowTransaction = false;//0否，1是
       // City cityBo=cityService.getCityByCode(city);
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(), city);
        if (configByCityCode != null) {
            allowTransaction = configByCityCode.getAllowTransaction() == null ? false : configByCityCode.getAllowTransaction();
            //取得该城市的坐标范围
            int srvGetFlag = orderReqVo.getSrvGetFlag() != null ? (int) orderReqVo.getSrvGetFlag() : 0;
            int srvReturnFlag = orderReqVo.getSrvReturnFlag() != null ? (int) orderReqVo.getSrvReturnFlag() : 0;
            List<Map<String, Double>> polygonPoints = new ArrayList<Map<String, Double>>();
            String addressRange = configByCityCode.getAddressRange();
            if (!StringUtils.isEmpty(addressRange)) {
                String[] ranges = addressRange.split("]");
                for(String range:ranges){
                    String str = range.substring(range.indexOf("[")+1, range.length());
                    String[] lonAndlat = str.split(",");
                    Map<String, Double> param = new HashMap<String,Double>();
                    param.put("lon", Double.parseDouble(lonAndlat[0]));
                    param.put("lat", Double.parseDouble(lonAndlat[1]));
                    log.info("param:{}",param);
                    polygonPoints.add(param);
                }
            }
            if (CollectionUtils.isEmpty(polygonPoints)) {
                return;
            }
            if (srvGetFlag==1&&!StringUtils.isEmpty(orderReqVo.getSrvGetLon()) && !StringUtils.isEmpty(orderReqVo.getSrvGetLat())) {
                Double x = Double.parseDouble(orderReqVo.getSrvGetLon()+"");
                Double y =  Double.parseDouble(orderReqVo.getSrvGetLat()+"");
                //校验取车是否在城市坐标内
                if (!GeoUtils.isPointInPolygon(x, y, polygonPoints)) {
                    // 判断是否凹凸取还车服务点
                    ServicePointEntity config = servicePointConfigSDK.getConfig(new DefaultConfigContext(), orderReqVo.getSrvGetAddr(), orderReqVo.getSrvGetLat(), orderReqVo.getSrvGetLon());
                    if (config == null) {
                        throw new CityLonLatException(ErrorCode.NO_RANGE_POINT.getCode(),ErrorCode.NO_RANGE_POINT.getText());
                    }
                }
            }
            if (srvReturnFlag==1&&!StringUtils.isEmpty(orderReqVo.getSrvReturnLon()) && !StringUtils.isEmpty(orderReqVo.getSrvReturnLat())) {
                Double x = Double.parseDouble(orderReqVo.getSrvReturnLon());
                Double y =  Double.parseDouble(orderReqVo.getSrvReturnLat());
                //校验还车是否在城市坐标内
                if (!GeoUtils.isPointInPolygon(x, y, polygonPoints)) {
                    // 判断是否凹凸取还车服务点
                    ServicePointEntity config = servicePointConfigSDK.getConfig(new DefaultConfigContext(), orderReqVo.getSrvReturnAddr(), orderReqVo.getSrvReturnLat(), orderReqVo.getSrvReturnLon());
                    if (config == null) {
                        throw new CityLonLatException(ErrorCode.NO_RANGE_POINT.getCode(),ErrorCode.NO_RANGE_POINT.getText());
                    }
                }
            }
            return;
        }
        if(!allowTransaction){
            throw new CityLonLatException(ErrorCode.SERVICE_NOT_OPEN.getCode(),ErrorCode.SERVICE_NOT_OPEN.getText());
        }
    }


}
