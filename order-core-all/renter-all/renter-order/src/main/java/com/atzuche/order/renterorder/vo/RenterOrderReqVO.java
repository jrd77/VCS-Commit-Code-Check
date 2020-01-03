package com.atzuche.order.renterorder.vo;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.autoyol.member.detail.vo.res.CommUseDriverInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生成租客订单参数封装类
 *
 * @author pengcheng.fu
 * @date 2019/12/27 17:34
 */

@Data
public class RenterOrderReqVO implements Serializable {

    private static final long serialVersionUID = -2219300643515658771L;

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 租客子订单号
     */
    private String renterOrderNo;

    /**
     * 订单类型
     */
    private String orderCategory;

    /**
     * 会员号
     */
    private String memNo;

    /**
     * 车辆注册号
     */
    private String carNo;

    /**
     * 车牌号
     */
    private String plateNum;

    /**
     * 应答标识位，0未设置，1已设置
     */
    private Integer replyFlag;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 订单来源
     */
    private Integer source;

    /**
     * 场景号
     */
    private String entryCode;

    /**
     * 订单取车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime rentTime;

    /**
     * 订单还车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime revertTime;

    /**
     * 是否购买补充保障
     */
    private String abatement;

    /**
     * 取送服务优惠券ID
     */
    private String getCarFreeCouponId;

    /**
     * 平台优惠券ID
     */
    private String disCouponIds;

    /**
     * 车主优惠券编码
     */
    private String carOwnerCouponNo;

    /**
     * 是否使用凹凸币:0.否 1.是
     */
    private Integer useAutoCoin;

    /**
     * 是否使用钱包余额:0.否 1.是
     */
    private Integer useBal;

    /**
     * 免押方式ID:1.绑卡免押 2.芝麻免押 3.支付押金
     */
    private String freeDoubleTypeId;

    /**
     * 取车服务标识
     */
    private Integer srvGetFlag;

    /**
     * 取车经度
     */
    private String srvGetLon;

    /**
     * 取车纬度
     */
    private String srvGetLat;

    /**
     * 还车服务标识
     */
    private Integer srvReturnFlag;

    /**
     * 还车经度
     */
    private String srvReturnLon;

    /**
     * 还车纬度
     */
    private String srvReturnLat;

    /**
     * 车辆经度
     */
    private String carLon;

    /**
     * 车辆纬度
     */
    private String carLat;

    /**
     * 提前时间（分钟数）
     */
    private Integer getCarBeforeTime;
    /**
     * 延后时间（分钟数）
     */
    private Integer returnCarAfterTime;

    /**
     * 车辆指导价格
     */
    private Integer guidPrice;

    /**
     * 车辆残值
     */
    private Integer carSurplusPrice;

    /**
     * 保费计算用购置价（保费购置价为空取车辆指导价算）
     */
    private Integer inmsrp;

    /**
     * 车辆品牌
     */
    private String brandId;

    /**
     * 车型
     */
    private String typeId;

    /**
     * 行驶证注册年月
     */
    private LocalDate licenseDay;

    /**
     * 驾驶证初次领证日期
     */
    private LocalDate certificationTime;

    /**
     * 是否是新用戶
     */
    private Boolean isNew;

    /**
     * 限时红包面额(管理后台，前端传值转换后的金额)
     */
    private Integer reductiAmt;

    /**
     * 是否使用特供价
     */
    private String useSpecialPrice;


    /**
     * 车辆标签
     */
    private List<String> labelIds;

    /**
     * 附加驾驶人列表
     */
    private List<String> driverIds;

    /**
     * 租期内每天价格
     */
    private List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;

    /**
     * 租客权益列表
     */
    private List<RenterMemberRightDTO> renterMemberRightDTOList;

    /**
     * 常用驾驶人列表
     */
    private List<CommUseDriverInfo> commUseDriverList;


}
