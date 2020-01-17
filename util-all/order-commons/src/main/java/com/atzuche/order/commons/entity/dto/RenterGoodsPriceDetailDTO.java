package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
public class RenterGoodsPriceDetailDTO {
    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 子订单号
     */
    @AutoDocProperty("子订单号")
    private String renterOrderNo;
    /**
     * 商品概览id
     */
    @AutoDocProperty("商品概览id")
    private Integer goodsId;
    /**
     * 天
     */
    @AutoDocProperty("天")
    private LocalDate carDay;
    
    @AutoDocProperty("天")
    private String carDayStr;
    
    /**
     * 天单价
     */
    @AutoDocProperty("天单价")
    private Integer carUnitPrice;
    /**
     * 小时数
     */
    @AutoDocProperty("小时数")
    private Float carHourCount;
    /**
     * 还车时间
     */
    @AutoDocProperty("还车时间")
    private LocalDateTime revertTime;
    
    
    
    
}
