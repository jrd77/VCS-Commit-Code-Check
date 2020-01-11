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
    private String memNo;
    private String carPlatNo;
    private int totalWallet;
    private int totalAutoCoin;

    private List<CarDayPrice> carSpecialDayPrices = new ArrayList<>();


    private List<CouponDTO> availablePlatCoupons = new ArrayList<>();
    private List<CouponDTO> availableGetCarCoupons = new ArrayList<>();
    private List<CouponDTO> availableCarOwnerCoupons = new ArrayList<>();


    @Data
    @ToString
    public static class CarDayPrice{
        private String day;
        private String desc;
        private String price;
    }

    @Data
    @ToString
    public static class CouponDTO{
        private String couponCode;
        private String couponName;
    }
}
