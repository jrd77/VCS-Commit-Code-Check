package com.atzuche.order.admin.vo.req.car;

import com.atzuche.order.commons.vo.res.consolecost.TempCarDepoistInfoResVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 保存车辆押金暂扣扣款信息
 *
 * @author pengcheng.fu
 * @date 2020/4/24 11:24
 */
@Data
public class CarDepositDetainInfoReqVO {

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "租客会员号", required = true)
    @NotBlank(message = "租客会员号不能为空")
    private String memNo;

    @AutoDocProperty(value = "订单车辆押金暂扣扣款信息列表", required = true)
    private List<TempCarDepoistInfoResVO> tempCarDepoists;

}
