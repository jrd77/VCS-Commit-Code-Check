package com.atzuche.order.renterorder.vo.owner;

import java.util.List;

import com.atzuche.order.rentercost.entity.vo.HolidayAverageResultVO;

import com.atzuche.order.rentercost.entity.vo.OwnerCouponLongVO;
import lombok.Data;

/**
 * 长租折扣券信息
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:03
 */
@Data
public class OwnerCouponLongResVO {

    private String resCode;

    private String resMsg;

    private OwnerCouponLongVO data;


}
