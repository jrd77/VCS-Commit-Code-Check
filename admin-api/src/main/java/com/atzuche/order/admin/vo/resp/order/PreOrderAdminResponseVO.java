package com.atzuche.order.admin.vo.resp.order;

import com.atzuche.order.commons.vo.res.order.CarOwnerCouponDetailVO;
import com.atzuche.order.commons.vo.res.order.DisCouponMemInfoVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:38 下午
 **/
@Data
@ToString
public class PreOrderAdminResponseVO {

    @AutoDocProperty(value = "用车城市编码")
    private String cityCode;
    @AutoDocProperty(value = "用车城市名称")
    private String rentCity;
    @AutoDocProperty(value="租客姓名")
    private String renterName;
    @AutoDocProperty(value="租客会员号")
    private String memNo;
    @AutoDocProperty(value="下单手机号")
    private String mobile;
    @AutoDocProperty(value="车主姓名")
    private String ownerName;
    @AutoDocProperty(value="车主会员号")
    private String ownerMemNo;
    @AutoDocProperty(value = "起租时间,格式yyyy-MM-dd HH:mm:ss")
    private String rentTime;
    @AutoDocProperty(value = "结束时间,格式yyyy-MM-dd HH:mm:ss")
    private String revertTime;
    @AutoDocProperty(value = "总天数")
    private String countDays;
    @AutoDocProperty(value="车牌号")
    private String carPlatNo;
    @AutoDocProperty(value="钱包总额")
    private int totalWallet;
    @AutoDocProperty(value="凹凸币总额")
    private int totalAutoCoin;
    @AutoDocProperty(value="特价车天单价")
    private List<CarDayPrice> carSpecialDayPrices = new ArrayList<>();
    @AutoDocProperty(value = "车主券列表")
    private List<CarOwnerCouponDetailVO> carOwnerCouponDetailVOList;
    @AutoDocProperty(value = "平台优惠券列表，不包括取还车")
    private List<DisCouponMemInfoVO> platCouponList;
    @AutoDocProperty(value = "取还车优惠券列表")
    private List<DisCouponMemInfoVO> getCarCouponList;


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
