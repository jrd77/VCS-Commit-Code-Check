package com.atzuche.order.renterorder.vo;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
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
     * 会员号
     */
    private String memNo;

    /**
     * 订单取车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime rentTime;

    /**
     * 订单还车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime revertTime;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 租期内每天价格
     */
    private List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;

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
     * 保费计算用购置价（保费购置价为空取车辆指导价算）
     */
    private Integer inmsrp;
    /**
     * 驾驶证初次领证日期
     */
    private LocalDate certificationTime;
    /**
     * 车辆标签
     */
    private List<String> labelIds;
    /**
     * 附加驾驶人id列表
     */
    private List<String> driverIds;


}
