package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:38 下午
 **/
@Data
@ToString
public class PreOrderAdminResponseVO {
    @AutoDocProperty(value="会员号")
    private String memNo;
    @AutoDocProperty(value="车牌号")
    private String carPlatNo;
    @AutoDocProperty(value="钱包总额")
    private int totalWallet;
    @AutoDocProperty(value="凹凸币总额")
    private int totalAutoCoin;

    @AutoDocProperty(value="特价车天单价")
    private List<CarDayPrice> carSpecialDayPrices = new ArrayList<>();

    @AutoDocProperty(value="可用的平台券")
    private List<CouponDTO> availablePlatCoupons = new ArrayList<>();
    @AutoDocProperty(value="可用的取还车券")
    private List<CouponDTO> availableGetCarCoupons = new ArrayList<>();
    @AutoDocProperty(value="可用的车主券")
    private List<CouponDTO> availableCarOwnerCoupons = new ArrayList<>();


    @Data
    @ToString
    public static class CarDayPrice{
        @AutoDocProperty(value="天")
        private String day;
        @AutoDocProperty(value="节假日描述")
        private String desc;
        @AutoDocProperty(value="价格")
        private String price;
    }

    @Data
    @ToString
    public static class CouponDTO{
        @AutoDocProperty(value="优惠券code")
        private String couponCode;
        @AutoDocProperty(value="优惠券名称")
        private String couponName;
    }
}
