package com.atzuche.order.delivery.vo.delivery;

import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 胡春林  新订单入库数据
 */
@Data
@ToString
public class OrderDeliveryDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 配送订单号
     */
    private String orderNoDelivery;
    /**
     * 配送类型 1-取车订单、2-还车订单
     */
    private Integer type;
    /**
     * 起租时间
     */
    private LocalDateTime rentTime;
    /**
     * 归还时间
     */
    private LocalDateTime revertTime;
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 车主姓名
     */
    private String ownerName;
    /**
     * 车主电话
     */
    private String ownerPhone;
    /**
     * 租客姓名
     */
    private String renterName;
    /**
     * 租客已成交次数
     */
    private Integer renterDealCount;
    /**
     * 租客电话
     */
    private String renterPhone;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 取还车人员
     */
    private String getReturnUserName;
    /**
     * 取还车人员手机号码
     */
    private String getReturnUserPhone;
    /**
     * 租客取还车地址
     */
    private String renterGetReturnAddr;
    /**
     * 租客取还车经度
     */
    private String renterGetReturnAddrLon;
    /**
     * 租客取还车维度
     */
    private String renterGetReturnAddrLat;
    /**
     * 车主取还车地址
     */
    private String ownerGetReturnAddr;
    /**
     * 车主取还车经度
     */
    private String ownerGetReturnAddrLon;
    /**
     * 车主取还车维度
     */
    private String ownerGetReturnAddrLat;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否通知到仁云 0-否，1-是
     */
    private Integer isNotifyRenyun;
    /**
     * 创建人
     */
    private String createOp;
    /**
     * 修改人
     */
    private String updateOp;
    
    /**
     * 提前或延后时间(取车:提前时间, 还车：延后时间)
     */
    private Integer aheadOrDelayTime;

    /**
     * 设置信息参数
     * @param orderType
     */
    public void setParamsTypeValue(OrderReqVO orderReqVO,Integer orderType) {
        if (orderType == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
           setRenterGetReturnAddr(orderReqVO.getSrvGetAddr());
           setRenterGetReturnAddrLat(orderReqVO.getSrvGetLat());
           setRenterGetReturnAddrLon(orderReqVO.getSrvGetLon());
        } else {
            setRenterGetReturnAddr(orderReqVO.getSrvReturnAddr());
            setRenterGetReturnAddrLat(orderReqVO.getSrvReturnLat());
            setRenterGetReturnAddrLon(orderReqVO.getSrvReturnLon());

        }
    }

}
