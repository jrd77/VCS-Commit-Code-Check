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
    private String ordernumber;
    private String servicetype;
    private String termtime;
    private String returntime;
    private String carno;
    private String vehiclemodel;
    private String vehicletype;
    private String deliverycarcity;
    private String pickupcaraddr;
    private String alsocaraddr;
    private String ownername;
    private String ownerphone;
    private String successordenumber;
    private String tenantname;
    private String tenantphone;
    private String tenantturnoverno;
    private String defaultpickupcaraddr;
    private String ownerType;
    private String sceneName;
    private String displacement;
    private String source;
    private String beforeTime;
    private String afterTime;
    private String getKilometre;
    private String returnKilometre;
    private String delegaAdmin;
    private String delegaAdminPhone;
    private String detectStatus;
    private String dayMileage;
    private String offlineOrderType;
    private String ssaRisks;
    private String emerContact;
    private String emerContactPhone;
    private String tankCapacity;
    private String realGetCarLon;
    private String realGetCarLat;
    private String realReturnCarLon;
    private String realReturnCarLat;
    private String carLon;
    private String carLat;
    private String oilPrice;
    private String depositPayTime;
    private String renterMemberFlag;
    private String useType;
    private String flightNo;
    private String guideDayPrice;
    private String sign;
    private String ownerNo;
    private String renterNo;
    private String ownerLables;
    private String tenantLables;
    private String tenantLevel;
    /**燃料*/
    private String engineType;
    /**天单价*/
    private String dayUnitPrice;
    private String holidayPrice;
    private String holidayAverage;
    private String rentAmt;

}



