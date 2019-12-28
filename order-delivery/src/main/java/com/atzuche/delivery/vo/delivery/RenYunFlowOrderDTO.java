package com.atzuche.delivery.vo.delivery;


import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 * 仁云新增订单需要的参数
 */
@Data
@ToString
public class RenYunFlowOrderDTO implements Serializable {

    private String takeNote;
    private Float year;
    private String riskControlAuditState;
    private String ownerGetAddr;
    private String ownerGetLon;
    private String ownerGetLat;
    private String ownerReturnAddr;
    private String ownerReturnLon;
    private String ownerReturnLat;
    private String orderType;
    private String channelType;
    private String oilScaleDenominator;
    /**订单编号**/
    private String ordernumber;
    /**服务类型（take:取车服务 back:还车服务）**/
    private String servicetype;
    /**起租时间**/
    private String termtime;
    /**归还时间**/
    private String returntime;
    /**车牌号**/
    private String carno;
    /**车型**/
    private String vehiclemodel;
    /**车辆类型**/
    private String vehicletype;
    /**交车所在市**/
    private String deliverycarcity;
    /**实际取车地址（只有取车服务才有）**/
    private String pickupcaraddr;
    /**实际还车地址（只有还车服务才有）**/
    private String alsocaraddr;
    /**车主姓名**/
    private String ownername;
    /**车主电话**/
    private String ownerphone;
    /**成功订单次数（此车辆）**/
    private String successordenumber;
    /**租客姓名**/
    private String tenantname;
    /**租客电话**/
    private String tenantphone;
    /**租客已成交次数**/
    private String tenantturnoverno;
    /**默认地址**/
    private String defaultpickupcaraddr;
    /**车辆使用类型**/
    private String ownerType;
    /**订单编号**/
    private String sceneName;
    /**订单编号**/
    private String displacement;
    /**订单编号**/
    private String source;
    /**订单编号**/
    private String beforeTime;
    /**订单编号**/
    private String afterTime;
    /**订单编号**/
    private String getKilometre;
    /**订单编号**/
    private String returnKilometre;
    /**订单编号**/
    private String delegaAdmin;
    /**订单编号**/
    private String delegaAdminPhone;
    /**订单编号**/
    private String detectStatus;
    /**订单编号**/
    private String dayMileage;
    /**订单编号**/
    private String offlineOrderType;
    /**订单编号**/
    private String ssaRisks;
    /**订单编号**/
    private String emerContact;
    /**订单编号**/
    private String emerContactPhone;
    /**订单编号**/
    private String tankCapacity;
    /**订单编号**/
    private String realGetCarLon;
    /**订单编号**/
    private String realGetCarLat;
    /**订单编号**/
    private String realReturnCarLon;
    /**订单编号**/
    private String realReturnCarLat;
    /**订单编号**/
    private String carLon;
    /**订单编号**/
    private String carLat;
    /**订单编号**/
    private String oilPrice;
    /**订单编号**/
    private String depositPayTime;
    /**订单编号**/
    private String renterMemberFlag;
    /**订单编号**/
    private String useType;
    /**订单编号**/
    private String flightNo;
    /**订单编号**/
    private String guideDayPrice;
    /**订单编号**/
    private String sign;
    /**订单编号**/
    private String ownerNo;
    /**订单编号**/
    private String renterNo;
    /**订单编号**/
    private String ownerLables;
    /**订单编号**/
    private String tenantLables;
    /**订单编号**/
    private String tenantLevel;
    /**燃料*/
    private String engineType;
    /**天单价*/
    private String dayUnitPrice;
    /**订单编号**/
    private String holidayPrice;
    /**订单编号**/
    private String holidayAverage;
    /**订单编号**/
    private String rentAmt;

}



