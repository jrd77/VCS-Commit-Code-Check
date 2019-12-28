package com.atzuche.delivery.vo.delivery;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 * 更新配送信息
 */
@Data
@ToString
public class UpdateFlowOrderDTO implements Serializable{

    /**
     * 订单编号
     **/
    private String ordernumber;
    /**
     * 服务类型（take:取车服务 back:还车服务）
     **/
    private String servicetype;
    /**
     * 变更类型（addr：地址变更 time:订单时间变更 ownerAddr: 车主取还车地址变更
     **/
    private String changetype;
    /**
     * 新实际取车地址
     **/
    private String newpickupcaraddr;
    /**
     * 新实际还车地址
     **/
    private String newalsocaraddr;

    /**
     * 新起租时间
     **/
    private String newtermtime;
    /**
     * 新归还时间
     **/
    private String newreturntime;
    /**
     * 默认地址
     **/
    private String defaultpickupcaraddr;
    /**
     * 预计取车时间（YYYYMMDDHHmmss)
     **/
    private String newbeforeTime;

    /**
     * 预计取车时间（YYYYMMDDHHmmss)
     **/
    private String newafterTime;

    /**
     * 预计取车公里数
     **/
    private String newgetKilometre;

    /**
     * 预计还车公里数
     **/
    private String newreturnKilometre;

    /**
     * 实际取车经度
     **/
    private String realGetCarLon;
    /**
     * 实际取车维度
     **/
    private String realGetCarLat;
    /**
     * 实际还车经度
     **/
    private String realReturnCarLon;
    /**
     * 实际还车维度
     **/
    private String realReturnCarLat;
    /**
     * 车辆经度（默认取车经度）
     **/
    private String carLon;
    /**
     * 车辆纬度（默认取车纬度）
     **/
    private String carLat;
    /**
     * 燃料
     */
    private String engineType;
    /**
     * 天单价
     */
    private String dayUnitPrice;
    /**
     * 天节日单价
     **/
    private String holidayPrice;
    /**
     * 天均价
     **/
    private String holidayAverage;
    /**
     * 租金
     **/
    private String rentAmt;
    /**
     * 油费单价
     **/
    private String oilPrice;
    /**
     * 租车押金支付时间,格式yyyyMMddHHmmss
     **/
    private String depositPayTime;
    /**
     * 车主取车地址
     **/
    private String ownerGetAddr;
    /**
     * 车主取车经度
     **/
    private String ownerGetLon;
    /**
     * 车主取车维度
     **/
    private String ownerGetLat;
    /**
     * 车主还车地址
     **/
    private String ownerReturnAddr;
    /**
     * 车主还车经度
     **/
    private String ownerReturnLon;
    /**
     * 车主还车维度
     **/
    private String ownerReturnLat;
    /**
     * 签名
     **/
    private String sign;

}
