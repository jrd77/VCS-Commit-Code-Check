package com.atzuche.order.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.order.delivery.vo.delivery.OrderDeliveryDTO;
import lombok.Data;
import lombok.ToString;


/**
 * 发送仁云信息表
 *@author 胡春林
 */
@Data
@ToString
public class OrderDeliveryFlowEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 订单类型（0,普通订单 1,代步车订单 2,携程套餐订单 3,携程到店订单 4,同程套餐订单 5,安联代步车订单 6,普通套餐订单 7,VIP订单
	 */
	private String orderType;
	/**
	 * 服务类型（take:取车服务 back:还车服务）
	 */
	private String serviceType;
	/**
	 * 取还车地址
	 */
	private String pickupAlsoCarAddr;
	/**
	 * 起租时间
	 */
	private LocalDateTime termTime;
	/**
	 * 归还时间
	 */
	private LocalDateTime returnTime;
	/**
	 * 车牌号
	 */
	private String carNo;
	/**
	 * 默认地址
	 */
	private String defaultPickupCarAddr;
	/**
	 * 车型
	 */
	private String vehicleModel;
	/**
	 * 车辆类型
	 */
	private String vehicleType;
	/**
	 * 交车所在市
	 */
	private String deliveryCarCity;
	/**
	 * 车主姓名
	 */
	private String ownerName;
	/**
	 * 车主电话
	 */
	private String ownerPhone;
	/**
	 * 成功订单次数（此车辆）
	 */
	private String successOrdeNumber;
	/**
	 * 租客姓名
	 */
	private String tenantName;
	/**
	 * 租客电话
	 */
	private String tenantPhone;
	/**
	 * 租客已成交次数
	 */
	private String tenantTurnoverNo;
	/**
	 * 车辆使用类型(仁云)
	 */
	private Integer ownerType;
	/**
	 * 订单来源场景
	 */
	private String sceneName;
	/**
	 * 排量
	 */
	private String displacement;
	/**
	 * 交易来源 1：手机，2：网站，3：管理后台, 4:CP B2C, 5: CP UPOP，6：携程，7:返利网，8:机场租车,10:H5一键租车,12：凹凸微信,13：APP分享下单
	 */
	private String source;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;


    public void setServiceTypeInfo(Integer orderType,OrderDeliveryDTO orderDeliveryDTO) {
        if (orderType == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            setServiceType(ServiceTypeEnum.TAKE_TYPE.getValue());
            setPickupAlsoCarAddr(orderDeliveryDTO.getRenterGetReturnAddr());
        } else {
            setServiceType(ServiceTypeEnum.BACK_TYPE.getValue());
            setPickupAlsoCarAddr(orderDeliveryDTO.getRenterGetReturnAddr());
        }
    }

}
