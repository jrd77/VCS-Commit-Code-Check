package com.atzuche.order.delivery.vo.trusteeship;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 胡春林
 * 托管车数据
 */
@Data
@ToString
public class OrderCarTrusteeshipVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 车管家姓名
     */
    private String trusteeshipName;
    /**
     * 车管家手机号
     */
    private String trusteeshipTelephone;
    /**
     * 出库时间
     */
    private LocalDateTime outDepotTime;
    /**
     * 入库时间
     */
    private LocalDateTime inDepotTime;
    /**
     * 出库里程数
     */
    private String outDepotMileage;
    /**
     * 入库里程数
     */
    private String inDepotMileage;
    /**
     * 出库油量: 1:1/16,2:2/16,3:3/16,4:4/16,5:5/16,6:6/16,7:7/16,8:8/16,9:9/16,10:10/16,11:11/16,12:12/16,13:13/16,14:14/16,15:15/16,16:16/16
     */
    private Integer outDepotOimass;
    /**
     * 入库油量: 1:1/16,2:2/16,3:3/16,4:4/16,5:5/16,6:6/16,7:7/16,8:8/16,9:9/16,10:10/16,11:11/16,12:12/16,13:13/16,14:14/16,15:15/16,16:16/16
     */
    private Integer inDepotOimass;
    /**
     * 入库是否损伤:1:是,2:否
     */
    private Integer inDepotDamage;
    /**
     * 行驶证是否正常交接: 1:是，2:否
     */
    private Integer drivingLicenseJoin;
    /**
     * 车钥匙是否正常交接: 1:是，2:否
     */
    private Integer carKeyJoin;
}
