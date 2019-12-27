package com.atzuche.order.renterorder.vo;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import lombok.Data;

import java.io.Serializable;
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
     * 租期内每天价格
     */
    private List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList;

    /**
     * 车辆标签集合
     */
    private List<String> labelIds;





}
