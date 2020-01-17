package com.atzuche.order.transport.utils;

import com.atzuche.order.commons.enums.ChannelNameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import static com.autoyol.platformcost.CommonUtils.calcDistance;

@Slf4j
public class GetReturnCarCostNeedUtil {

    /**
     * 计算取还车距离
     * @return
     */
    public static Float getRealDistance(String carLon,String carLat,String origionCarLon,String originCarLat){
        try {
            if(StringUtils.isBlank(carLon) || StringUtils.isBlank(carLat)
                    || StringUtils.isBlank(origionCarLon) || StringUtils.isBlank(originCarLat)) {

                return 0F;
            }
            return (float) calcDistance(Double.valueOf(carLon),Double.valueOf(carLat),
                    Double.valueOf(origionCarLon), Double.valueOf(originCarLat));
        } catch (Exception e) {
            log.error("getRealDistance计算取还车距离报错距离返回0：",e);
        }
        return 0F;
    }


    /**
     * 根据entryCode判断渠道
     * @param entryCode
     * @return
     */
    public static ChannelNameTypeEnum getChannelCodeByEntryCode(String entryCode){
        if(StringUtils.isEmpty(entryCode)){
            return ChannelNameTypeEnum.APP;
        }

        //ota平台  EX021只代表订单为套餐
        if(entryCode.equals("ota")){
            return ChannelNameTypeEnum.OTA;
        }
        //代步车渠道、安联
        else if(entryCode.equals("EX011") || entryCode.equals("EX022") ||
                entryCode.equals("EX030") || entryCode.equals("scooter")){
            return ChannelNameTypeEnum.SCOOTER;
        }
        //App
        else if(entryCode.equals("app")){
            return ChannelNameTypeEnum.APP;
        }

        return ChannelNameTypeEnum.APP;
    }

    /**
     * 根据来源判断渠道
     * @param source
     * @return
     */
    public static ChannelNameTypeEnum getChannelCode(Integer source){
        if(null == source){
            return ChannelNameTypeEnum.APP;
        }

        //携程：400，同程：401，平安
        if(source.intValue() == 400 || source.intValue() == 401 || source.intValue() == 402){
            return ChannelNameTypeEnum.OTA;
        }

        return ChannelNameTypeEnum.APP;
    }


    /**
     *
     * 取还车订单类型
     * @param isPackageOrder
     * @return
     */
    public static String getIsPackageOrder(Boolean isPackageOrder){
        if(isPackageOrder != null && isPackageOrder){
            // 套餐订单
            return "package";
        }else{
            // 普通订单
            return "general";
        }
    }
}
