package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName UserOrdersRestult
 * @Author yusong.miao
 * @Date 2019/8/9 14:53
 * @Version 1.0
 */
@Data
public class UserInvoiceOrdersVO {

        @AutoDocProperty(value = "订单号")
        private String orderNo;
        @AutoDocProperty("租客子订单号")
        private String renterOrderNo;

        @AutoDocProperty(value = "车辆名称")
        private String vehicleName;

        @AutoDocProperty(value = "车辆图片")
        private String vehiclePic;

        @AutoDocProperty(value = "每日里程限制")
        private String dayMileageLimited;

        @AutoDocProperty(value = "订单金额(元)")
        private String orderMoney;

        @AutoDocProperty(value = "是否本地车牌 0 是 1 否")
        private String natives;

        @AutoDocProperty(value = "订单类型 0 普通 1 套餐 ")
        private String orderType;

        @AutoDocProperty(value = "变速箱类型 2 自动 1 手动")
        private String gearboxType;

        @AutoDocProperty(value = "排量")
        private String vehicleDisplacement;

        @AutoDocProperty(value = "车型")
        private String vehicleType;

        @AutoDocProperty(value = "车系")
        private String vehicleSeries;

        @AutoDocProperty(value = "车牌号")
        private String plateNumber;

        @AutoDocProperty(value = "订单完成时间")
        private String finishTime;

        @AutoDocProperty(value = "可开票金额")
        private String invoiceAmountGross;
        //内部使用
        private String renterMemNo;


}
