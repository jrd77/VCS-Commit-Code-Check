package com.atzuche.order.delivery.vo.handover;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 胡春林
 * 租客交接车信息
 */
@Data
@ToString
public class HandoverCarInfoDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 交车类型 1-车主向租客交车、2-租客向车主交车、3-车管家向租客交车、4-租客向车管家交车
     */
    private Integer type;
    /**
     * 交车时油耗刻度
     */
    private Integer oilNum;
    /**
     * 交车时里程数
     */
    private Integer mileageNum;
    /**
     * 延后时间（分钟）
     */
    private Integer delayTime;
    /**
     * 提前时间（分钟）
     */
    private Integer aheadTime;
    /**
     * 实际交车地址
     */
    private String realReturnAddr;
    /**
     * 实际交车经度
     */
    private String realReturnAddrLon;
    /**
     * 实际交车纬度
     */
    private String realReturnAddrLat;
    /**
     * 交车时间
     */
    private LocalDateTime realReturnTime;
    /**
     * 交车人姓名
     */
    private String realReturnUserName;
    /**
     * 交车人电话号码
     */
    private String realReturnUserPhone;
    /**
     * 交车人会员号
     */
    private Integer realReturnMemNo;
    /**
     * 取车人姓名
     */
    private String realGetUserName;
    /**
     * 取车人电话号码
     */
    private String realGetUserPhone;
    /**
     * 取车人会员号
     */
    private Integer realGetMemNo;
    /**
     * 图片文件夹路径
     */
    private String imageUrl;
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     */
    private String createOp;

    /**
     * 消息ID
     */
    private String msgId;


    public void setAheadTimeAndType(Integer getMinutes, Integer returnMinutes) {
        if (getMinutes != null) {
            setAheadTime(getMinutes);
            setType(RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue());
        } else if (returnMinutes != null) {
            setDelayTime(returnMinutes);
            setType(RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue());
        }
    }

}
