package com.atzuche.order.admin.vo.resp;

import com.atzuche.order.commons.vo.res.order.CarOwnerCouponDetailVO;
import com.atzuche.order.commons.vo.res.order.DisCouponMemInfoVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 4:06 下午
 **/
@Data
@ToString
public class MemAvailableCouponVO {
    @AutoDocProperty(value = "车主券列表")
    private List<CarOwnerCouponDetailVO> carOwnerCouponDetailVOList;
    @AutoDocProperty(value = "平台优惠券列表，不包括取还车")
    private List<DisCouponMemInfoVO> platCouponList;
    @AutoDocProperty(value = "取还车优惠券列表")
    private List<DisCouponMemInfoVO> getCarCouponList;
}
