package com.atzuche.order.commons.vo.res.consolecost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * 车辆押金暂扣扣款处理信息
 *
 * @author pengcheng.fu
 * @date 2020/4/23 17:15
 */

@Data
public class TempCarDepoistInfoResVO {

    @AutoDocProperty(value = "主键")
    private Integer id;

    @AutoDocProperty(value = "费用编码")
    private String cashNo;

    @AutoDocProperty(value = "费用标题")
    private String cashTitle;

    @AutoDocProperty(value = "费用金额")
    private Integer cashAmt;

}
