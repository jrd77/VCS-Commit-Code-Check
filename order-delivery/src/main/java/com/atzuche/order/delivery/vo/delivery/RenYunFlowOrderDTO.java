package com.atzuche.order.delivery.vo.delivery;


import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
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

    /**
     * 取车备注
     **/
    private String takeNote;
    /**
     * 车龄
     **/
    private Float year;
    /**
     * 风控审核状态
     **/
    private String riskControlAuditState;

    /**
     * 车主会员号
     **/
    private String ownerNo;
    /**
     * 租客会员号
     **/
    private String renterNo;
    /**
     * 订单类型（0,普通订单 1,代步车订单 2,携程套餐订单 3,携程到店订单 4,同程套餐订单 5,安联代步车订单 6,普通套餐订单 7,VIP订单
     * ）
     **/
    private String orderType;
    /**
     * 渠道（0,短租渠道 1,代步车渠道 2,BD渠道订单 3,VIP渠道）
     **/
    private String channelType;

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
     * 油量刻度分母
     **/
    private String oilScaleDenominator;
    /**
     * 订单编号
     **/
    private String ordernumber;
    /**
     * 服务类型（take:取车服务 back:还车服务）
     **/
    private String servicetype;
    /**
     * 起租时间
     **/
    private String termtime;
    /**
     * 归还时间
     **/
    private String returntime;
    /**
     * 车牌号
     **/
    private String carno;
    /**
     * 车型
     **/
    private String vehiclemodel;
    /**
     * 车辆类型
     **/
    private String vehicletype;
    /**
     * 交车所在市
     **/
    private String deliverycarcity;
    /**
     * 实际取车地址（只有取车服务才有）
     **/
    private String pickupcaraddr;
    /**
     * 实际还车地址（只有还车服务才有）
     **/
    private String alsocaraddr;
    /**
     * 车主姓名
     **/
    private String ownername;
    /**
     * 车主电话
     **/
    private String ownerphone;
    /**
     * 成功订单次数（此车辆）
     **/
    private String successordenumber;
    /**
     * 租客姓名
     **/
    private String tenantname;
    /**
     * 租客电话
     **/
    private String tenantphone;
    /**
     * 租客已成交次数
     **/
    private String tenantturnoverno;
    /**
     * 默认地址
     **/
    private String defaultpickupcaraddr;
    /**
     * 车辆使用类型
     **/
    private String ownerType;
    /**
     * 订单来源场景
     **/
    private String sceneName;
    /**
     * 排量
     **/
    private String displacement;
    /**
     * 交易来源
     * 1：手机，2：网站，3：管理后台, 4:CP B2C, 5: CP UPOP，6：携程，7:返利网，8:机场租车,10:H5一键租车,12：凹凸微信,13：APP分享下单
     **/
    private String source;
    /**
     * 预计取车时间（YYYY-MM-DD HH:mm)
     **/
    private String beforeTime;
    /**
     * 预计还车时间（YYYY-MM-DD HH:mm)
     **/
    private String afterTime;
    /**
     * 预计取车公里数
     **/
    private String getKilometre;
    /**
     * 预计还车公里数
     **/
    private String returnKilometre;
    /**
     * 代管车管理员
     **/
    private String delegaAdmin;
    /**
     * 代管车管理员手机号
     **/
    private String delegaAdminPhone;
    /**
     * 车辆最新检测状态（检测状态: 检测状态:1,未检测;2,检测通过;3,检测通过-建议改善;4,高危车辆-已修复;5,高危车辆-未修复6 .未检测-已报名7 .检测失效）
     **/
    private String detectStatus;
    /**
     * 日限里程
     **/
    private String dayMileage;
    /**
     * 线下订单类型（1:三方合同订单,2:托管车长租订单,3:旅游产品下单,4太保出险代步车（未满足））
     **/
    private String offlineOrderType;
    /**
     * 超级补充全
     **/
    private String ssaRisks;
    /**
     * 紧急联系人
     **/
    private String emerContact;
    /**
     * 紧急联系人电话
     **/
    private String emerContactPhone;
    /**
     * 油箱容量
     **/
    private String tankCapacity;
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
     * 油费单价
     **/
    private String oilPrice;
    /**
     * 租车押金支付时间,格式yyyyMMddHHmmss
     **/
    private String depositPayTime;
    /**
     * 会员标识
     **/
    private String renterMemberFlag;
    /**
     * 功能类型
     **/
    private String useType;
    /**
     * 航班号
     **/
    private String flightNo;
    /**
     * 日租金指导价
     **/
    private String guideDayPrice;
    /**
     * 签名
     **/
    private String sign;
    /**
     * 车主的标签
     **/
    private String ownerLables;
    /**
     * 租客的标签
     **/
    private String tenantLables;
    /**
     * 租客的等级
     **/
    private String tenantLevel;
    /**
     * 车主的等级
     **/
    private String ownerLevel;
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
     * 合作方
     **/
    private String partner;

    public void setServiceTypeInfo(Integer orderType,OrderDeliveryDTO orderDeliveryDTO) {
        if (orderType == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            setServicetype(ServiceTypeEnum.TAKE_TYPE.getValue());
            setPickupcaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
        } else {
            setServicetype(ServiceTypeEnum.BACK_TYPE.getValue());
            setAlsocaraddr(orderDeliveryDTO.getRenterGetReturnAddr());
        }
    }

}



